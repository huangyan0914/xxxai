param(
    [string]$BaseUrl = "http://localhost:9901",
    [int]$TimeoutSec = 20
)

# 编码说明：
# - Windows PowerShell 5.x 下无 BOM 的 UTF-8 脚本会导致中文常量解析错误；响应 JSON 也常被按系统代码页解码而乱码。
# - 本脚本测试数据尽量使用 ASCII；HTTP 统一按 UTF-8 解析响应体。
# - 若要在脚本里写中文断言，请保存为「UTF-8 带 BOM」，或使用 PowerShell 7+（pwsh）执行。

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
if ($PSVersionTable.PSVersion.Major -lt 6) {
    try {
        [Console]::OutputEncoding = [System.Text.Encoding]::UTF8
        $OutputEncoding = [System.Text.Encoding]::UTF8
    } catch { }
}

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "========== $Message ==========" -ForegroundColor Cyan
}

function Fail {
    param([string]$Message)
    throw "[FAILED] $Message"
}

function Assert-True {
    param(
        [bool]$Condition,
        [string]$Message
    )
    if (-not $Condition) {
        Fail $Message
    }
}

function Assert-Equal {
    param(
        $Actual,
        $Expected,
        [string]$Message
    )
    if ($Actual -ne $Expected) {
        Fail "$Message | expected=[$Expected], actual=[$Actual]"
    }
}

function Assert-Contains {
    param(
        [object[]]$Collection,
        $ExpectedItem,
        [string]$Message
    )
    if (-not ($Collection -contains $ExpectedItem)) {
        $json = $Collection | ConvertTo-Json -Depth 10 -Compress
        Fail "$Message | expected item [$ExpectedItem] not found in [$json]"
    }
}

function Read-ResponseJsonUtf8 {
    param($WebResponse)

    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    if ($null -ne $WebResponse.RawContentStream) {
        try {
            $stream = $WebResponse.RawContentStream
            if ($stream.CanSeek) {
                $null = $stream.Seek(0, [System.IO.SeekOrigin]::Begin)
            }
            $ms = New-Object System.IO.MemoryStream
            try {
                $stream.CopyTo($ms)
                return $utf8NoBom.GetString($ms.ToArray())
            } finally {
                $ms.Dispose()
            }
        } catch { }
    }
    if ($null -ne $WebResponse.Content -and $WebResponse.Content.Length -gt 0) {
        $latin1 = [System.Text.Encoding]::GetEncoding(28591)
        $bytes = $latin1.GetBytes($WebResponse.Content)
        return $utf8NoBom.GetString($bytes)
    }
    return ""
}

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        $Body = $null,
        [string]$RawJson = ""
    )

    $uri = "$BaseUrl$Path"
    $params = @{
        Uri             = $uri
        Method          = $Method
        TimeoutSec      = $TimeoutSec
        ErrorAction     = "Stop"
        UseBasicParsing = $true
    }

    $bodyStr = $null
    if ($RawJson -ne "") {
        $bodyStr = $RawJson
    }
    elseif ($null -ne $Body) {
        $bodyStr = ($Body | ConvertTo-Json -Depth 20 -Compress)
    }

    if ($null -ne $bodyStr -and $Method -ne "GET") {
        $params["ContentType"] = "application/json; charset=utf-8"
        $params["Body"] = [System.Text.Encoding]::UTF8.GetBytes($bodyStr)
    }

    Write-Host "$Method $uri"

    $jsonText = $null
    if ($PSVersionTable.PSVersion.Major -ge 6) {
        $paramsRest = @{
            Uri             = $uri
            Method          = $Method
            TimeoutSec      = $TimeoutSec
            ErrorAction     = "Stop"
        }
        if ($null -ne $bodyStr -and $Method -ne "GET") {
            $paramsRest["ContentType"] = "application/json; charset=utf-8"
            $paramsRest["Body"] = [System.Text.Encoding]::UTF8.GetBytes($bodyStr)
        }
        $resp = Invoke-RestMethod @paramsRest
        Assert-Equal $resp.code "0" "api response code is not success"
        return $resp
    }

    $wr = Invoke-WebRequest @params
    $jsonText = Read-ResponseJsonUtf8 -WebResponse $wr
    if ([string]::IsNullOrWhiteSpace($jsonText)) {
        Fail "empty response body from API"
    }
    $resp = $jsonText | ConvertFrom-Json
    Assert-Equal $resp.code "0" "api response code is not success"
    return $resp
}

function Get-PageRecords {
    param($PageResp)
    if ($null -eq $PageResp.data) {
        return @()
    }
    if ($null -eq $PageResp.data.records) {
        return @()
    }
    return @($PageResp.data.records)
}

function Find-RecordByField {
    param(
        [object[]]$Records,
        [string]$FieldName,
        [string]$ExpectedValue
    )
    foreach ($item in $Records) {
        if ($item.$FieldName -eq $ExpectedValue) {
            return $item
        }
    }
    return $null
}

function Find-PermissionByCodeInTree {
    param(
        [object[]]$Nodes,
        [string]$PermCode
    )
    foreach ($n in $Nodes) {
        if ($null -eq $n) { continue }
        if ($n.permCode -eq $PermCode) {
            return $n
        }
        if ($null -ne $n.children -and @($n.children).Count -gt 0) {
            $found = Find-PermissionByCodeInTree -Nodes @($n.children) -PermCode $PermCode
            if ($null -ne $found) {
                return $found
            }
        }
    }
    return $null
}

Write-Host "BaseUrl=$BaseUrl, TimeoutSec=$TimeoutSec" -ForegroundColor Yellow
$suffix = (Get-Date -Format "yyyyMMddHHmmss")
Write-Host "Run suffix=$suffix" -ForegroundColor Yellow

$userId = $null
$userGroupId = $null
$roleId = $null
$permMenuId = $null
$permButtonId = $null

try {
    Write-Step "1) Permission: create MENU root -> create BUTTON -> GET tree"
    $menuCode = "AUTO-MENU-$suffix"
    $createMenuBody = @{
        parentId   = $null
        permType   = "MENU"
        permCode   = $menuCode
        permName   = "AUTO-MENU-NAME-$suffix"
        path       = "/auto-menu"
        component  = "Layout"
        icon       = "menu"
        sortOrder  = 1
        enabled    = 1
    }
    $createMenuResp = Invoke-Api -Method "POST" -Path "/api/system/permissions" -Body $createMenuBody
    Assert-True ([bool]$createMenuResp.data) "create menu permission failed"
    $menuPage = Invoke-Api -Method "GET" -Path "/api/system/permissions/tree"
    $menuRecord = Find-PermissionByCodeInTree -Nodes @($menuPage.data) -PermCode $menuCode
    Assert-True ($null -ne $menuRecord) "menu permission not found in tree"
    $permMenuId = [long]$menuRecord.id
    Assert-True ($permMenuId -gt 0) "invalid menu permission id"

    $buttonCode = "AUTO-BTN-$suffix"
    $createButtonBody = @{
        parentId   = $permMenuId
        permType   = "BUTTON"
        permCode   = $buttonCode
        permName   = "AUTO-BTN-NAME-$suffix"
        path       = $null
        component  = $null
        icon       = $null
        sortOrder  = 1
        enabled    = 1
    }
    $createButtonResp = Invoke-Api -Method "POST" -Path "/api/system/permissions" -Body $createButtonBody
    Assert-True ([bool]$createButtonResp.data) "create button permission failed"

    $tree2 = Invoke-Api -Method "GET" -Path "/api/system/permissions/tree"
    $buttonRecord = Find-PermissionByCodeInTree -Nodes @($tree2.data) -PermCode $buttonCode
    Assert-True ($null -ne $buttonRecord) "button permission not found under tree"
    $permButtonId = [long]$buttonRecord.id
    Assert-True ($permButtonId -gt 0) "invalid button permission id"

    $updateMenuBody = @{
        parentId   = $null
        permType   = "MENU"
        permCode   = $menuCode
        permName   = "AUTO-MENU-NAME-UPD-$suffix"
        path       = "/auto-menu-upd"
        component  = "Layout"
        icon       = "menu"
        sortOrder  = 2
        enabled    = 1
    }
    $updMenu = Invoke-Api -Method "PUT" -Path "/api/system/permissions/$permMenuId" -Body $updateMenuBody
    Assert-True ([bool]$updMenu.data) "update menu permission failed"

    Write-Step "2) Role: create -> page -> bind permissions -> data scope ALL -> SPECIFIC"
    $roleName = "AUTO-ROLE-$suffix"
    $createRoleBody = @{
        roleName = $roleName
        remark   = "http check role"
    }
    $createRoleResp = Invoke-Api -Method "POST" -Path "/api/system/roles" -Body $createRoleBody
    Assert-True ([bool]$createRoleResp.data) "create role failed"

    $rolePage = Invoke-Api -Method "GET" -Path "/api/system/roles?pageNum=1&pageSize=20&roleName=$roleName"
    $roleRecords = Get-PageRecords $rolePage
    $roleRecord = Find-RecordByField -Records $roleRecords -FieldName "roleName" -ExpectedValue $roleName
    Assert-True ($null -ne $roleRecord) "role not found in page"
    $roleId = [long]$roleRecord.id
    Assert-True ($roleId -gt 0) "invalid role id"

    $roleDetail = Invoke-Api -Method "GET" -Path "/api/system/roles/$roleId"
    Assert-Equal $roleDetail.data.roleName $roleName "role detail name mismatch"

    $bindPermBody = @{ ids = @($permMenuId, $permButtonId) }
    Invoke-Api -Method "PUT" -Path "/api/system/roles/$roleId/permissions" -Body $bindPermBody | Out-Null
    $permIdsResp = Invoke-Api -Method "GET" -Path "/api/system/roles/$roleId/permissions"
    Assert-Contains -Collection @($permIdsResp.data) -ExpectedItem $permMenuId -Message "role permissions missing menu id"
    Assert-Contains -Collection @($permIdsResp.data) -ExpectedItem $permButtonId -Message "role permissions missing button id"

    $scopeAllBody = @{
        dataResourceType = "TRAINING_RESOURCE"
        dataScopeType    = "ALL"
        items            = $null
    }
    Invoke-Api -Method "PUT" -Path "/api/system/roles/$roleId/data-scopes" -Body $scopeAllBody | Out-Null
    $scopeGet = Invoke-Api -Method "GET" -Path "/api/system/roles/$roleId/data-scopes?dataResourceType=TRAINING_RESOURCE"
    Assert-Equal $scopeGet.data.dataScopeType "ALL" "data scope should be ALL"

    $dummyItemId = 999999001
    $scopeSpecBody = @{
        dataResourceType = "TRAINING_RESOURCE"
        dataScopeType    = "SPECIFIC"
        items            = @(
            @{ itemType = "TRAINING_RESOURCE"; itemId = $dummyItemId }
        )
    }
    Invoke-Api -Method "PUT" -Path "/api/system/roles/$roleId/data-scopes" -Body $scopeSpecBody | Out-Null
    $scopeGet2 = Invoke-Api -Method "GET" -Path "/api/system/roles/$roleId/data-scopes?dataResourceType=TRAINING_RESOURCE"
    Assert-Equal $scopeGet2.data.dataScopeType "SPECIFIC" "data scope should be SPECIFIC"
    $itemIds = @($scopeGet2.data.items | ForEach-Object { [long]$_.itemId })
    Assert-Contains -Collection $itemIds -ExpectedItem $dummyItemId -Message "data scope item id missing"

    $updateRoleBody = @{
        roleName = "$roleName-UPDATED"
        remark   = "http check role updated"
    }
    $updRole = Invoke-Api -Method "PUT" -Path "/api/system/roles/$roleId" -Body $updateRoleBody
    Assert-True ([bool]$updRole.data) "update role failed"

    Write-Step "3) User group: create -> members -> group roles"
    $groupName = "AUTO-GROUP-$suffix"
    $createGroupBody = @{
        groupName = $groupName
        remark    = "http check group"
    }
    $createGroupResp = Invoke-Api -Method "POST" -Path "/api/system/user-groups" -Body $createGroupBody
    Assert-True ([bool]$createGroupResp.data) "create user group failed"

    $groupPage = Invoke-Api -Method "GET" -Path "/api/system/user-groups?pageNum=1&pageSize=20&groupName=$groupName"
    $groupRecords = Get-PageRecords $groupPage
    $groupRecord = Find-RecordByField -Records $groupRecords -FieldName "groupName" -ExpectedValue $groupName
    Assert-True ($null -ne $groupRecord) "user group not found in page"
    $userGroupId = [long]$groupRecord.id

    $bindGroupRoleBody = @{ ids = @($roleId) }
    Invoke-Api -Method "PUT" -Path "/api/system/user-groups/$userGroupId/roles" -Body $bindGroupRoleBody | Out-Null
    $groupRoleIds = Invoke-Api -Method "GET" -Path "/api/system/user-groups/$userGroupId/roles"
    Assert-Contains -Collection @($groupRoleIds.data) -ExpectedItem $roleId -Message "group role binding missing"

    Write-Step "4) User: create -> page -> update -> password reset -> direct roles -> group members"
    $loginName = "auto_user_$suffix"
    $createUserBody = @{
        username   = $loginName
        password   = "InitP@ss1"
        realName   = "AUTO-USER-$suffix"
        gender     = "male"
        studentNo  = "SN$suffix"
        phone      = "13800000000"
        email      = "auto_$suffix@test.local"
        status     = "normal"
    }
    $createUserResp = Invoke-Api -Method "POST" -Path "/api/system/users" -Body $createUserBody
    $userId = [long]$createUserResp.data
    Assert-True ($userId -gt 0) "invalid user id from create"

    $userPage = Invoke-Api -Method "GET" -Path "/api/system/users?pageNum=1&pageSize=20&username=$loginName"
    $userRecords = Get-PageRecords $userPage
    $userRecord = Find-RecordByField -Records $userRecords -FieldName "username" -ExpectedValue $loginName
    Assert-True ($null -ne $userRecord) "user not found in page"

    $userDetail = Invoke-Api -Method "GET" -Path "/api/system/users/$userId"
    Assert-Equal $userDetail.data.username $loginName "user detail username mismatch"

    $updateUserBody = @{
        realName = "AUTO-USER-UPDATED-$suffix"
        status   = "normal"
        phone    = "13900000001"
    }
    Invoke-Api -Method "PUT" -Path "/api/system/users/$userId" -Body $updateUserBody | Out-Null
    $userDetail2 = Invoke-Api -Method "GET" -Path "/api/system/users/$userId"
    Assert-Equal $userDetail2.data.realName "AUTO-USER-UPDATED-$suffix" "user realName update mismatch"

    $resetBody = @{ newPassword = "ResetP@ss2" }
    $resetResp = Invoke-Api -Method "POST" -Path "/api/system/users/$userId/password/reset" -Body $resetBody
    Assert-Equal $resetResp.data.newPasswordPlain "ResetP@ss2" "reset password plain mismatch"

    $bindUserRoleBody = @{ ids = @($roleId) }
    Invoke-Api -Method "PUT" -Path "/api/system/users/$userId/roles" -Body $bindUserRoleBody | Out-Null
    $userRoleIds = Invoke-Api -Method "GET" -Path "/api/system/users/$userId/roles"
    Assert-Contains -Collection @($userRoleIds.data) -ExpectedItem $roleId -Message "user direct roles missing"

    $memberBody = @{ ids = @($userId) }
    Invoke-Api -Method "PUT" -Path "/api/system/user-groups/$userGroupId/members" -Body $memberBody | Out-Null
    $memberIds = Invoke-Api -Method "GET" -Path "/api/system/user-groups/$userGroupId/members"
    Assert-Contains -Collection @($memberIds.data) -ExpectedItem $userId -Message "group members missing user"

    $userDetail3 = Invoke-Api -Method "GET" -Path "/api/system/users/$userId"
    $gn = @($userDetail3.data.groupNames)
    Assert-True ($gn -contains $groupName) "user detail should list group name [$groupName], got [$($gn -join ',')]"

    $userPageRoleFilter = Invoke-Api -Method "GET" -Path "/api/system/users?pageNum=1&pageSize=20&roleId=$roleId"
    $userRecordsByRole = Get-PageRecords $userPageRoleFilter
    $userByRole = Find-RecordByField -Records $userRecordsByRole -FieldName "username" -ExpectedValue $loginName
    Assert-True ($null -ne $userByRole) "user page filter by roleId should include created user"

    $userPageGroupFilter = Invoke-Api -Method "GET" -Path "/api/system/users?pageNum=1&pageSize=20&userGroupId=$userGroupId"
    $userRecordsByGroup = Get-PageRecords $userPageGroupFilter
    $userByGroup = Find-RecordByField -Records $userRecordsByGroup -FieldName "username" -ExpectedValue $loginName
    Assert-True ($null -ne $userByGroup) "user page filter by userGroupId should include created user"

    Write-Step "5) Oper log: append -> page"
    $logBody = @{
        module        = "system"
        bizType       = "user"
        bizId         = "$userId"
        action        = "http_check"
        operatorId    = $userId
        operatorName  = $loginName
        requestUri    = "/api/system/oper-logs"
        detail        = "suffix=$suffix"
    }
    Invoke-Api -Method "POST" -Path "/api/system/oper-logs" -Body $logBody | Out-Null
    $logPage = Invoke-Api -Method "GET" -Path "/api/system/oper-logs?pageNum=1&pageSize=20&module=system&action=http_check"
    $logRecords = Get-PageRecords $logPage
    $logHit = $false
    foreach ($lr in $logRecords) {
        if ($lr.bizId -eq "$userId" -and $lr.action -eq "http_check") {
            $logHit = $true
            break
        }
    }
    Assert-True $logHit "oper log page should contain appended record"

    Write-Step "6) Cleanup: clear bindings -> delete user / group / role / permissions"
    Invoke-Api -Method "PUT" -Path "/api/system/users/$userId/roles" -Body @{ ids = @() } | Out-Null
    Invoke-Api -Method "PUT" -Path "/api/system/user-groups/$userGroupId/members" -Body @{ ids = @() } | Out-Null
    Invoke-Api -Method "PUT" -Path "/api/system/user-groups/$userGroupId/roles" -Body @{ ids = @() } | Out-Null

    Invoke-Api -Method "DELETE" -Path "/api/system/users/$userId" | Out-Null
    $userId = $null

    Invoke-Api -Method "DELETE" -Path "/api/system/user-groups/$userGroupId" | Out-Null
    $userGroupId = $null

    Invoke-Api -Method "PUT" -Path "/api/system/roles/$roleId/permissions" -Body @{ ids = @() } | Out-Null
    $scopeClearBody = @{
        dataResourceType = "TRAINING_RESOURCE"
        dataScopeType    = "ALL"
        items            = $null
    }
    Invoke-Api -Method "PUT" -Path "/api/system/roles/$roleId/data-scopes" -Body $scopeClearBody | Out-Null
    Invoke-Api -Method "DELETE" -Path "/api/system/roles/$roleId" | Out-Null
    $roleId = $null

    Invoke-Api -Method "DELETE" -Path "/api/system/permissions/$permButtonId" | Out-Null
    $permButtonId = $null
    Invoke-Api -Method "DELETE" -Path "/api/system/permissions/$permMenuId" | Out-Null
    $permMenuId = $null

    Write-Host ""
    Write-Host "[PASS] All system RBAC interface checks passed." -ForegroundColor Green
}
catch {
    Write-Host ""
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host "[FAIL] Script failed. Created data may need manual cleanup. IDs below." -ForegroundColor Red
    Write-Host "userId=$userId, userGroupId=$userGroupId, roleId=$roleId, permMenuId=$permMenuId, permButtonId=$permButtonId" -ForegroundColor Yellow
    exit 1
}
