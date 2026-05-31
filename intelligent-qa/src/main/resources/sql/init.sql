-- 智能问答模块 DDL
-- 表前缀: t_qa_

-- 用户 TempToken 映射表（每个用户对应一个 LazyCraft 临时 Token）
CREATE TABLE IF NOT EXISTS t_qa_user_token
(
    id          BIGSERIAL PRIMARY KEY,
    user_id     VARCHAR(64)  NOT NULL,
    temp_token  VARCHAR(512) NOT NULL,
    deleted     INT          NOT NULL DEFAULT 0,
    creator     VARCHAR(64),
    create_time TIMESTAMP,
    updater     VARCHAR(64),
    update_time TIMESTAMP
);

COMMENT ON TABLE t_qa_user_token IS '用户 LazyCraft TempToken 映射';
COMMENT ON COLUMN t_qa_user_token.user_id IS '系统用户 ID';
COMMENT ON COLUMN t_qa_user_token.temp_token IS 'LazyCraft 临时 Token';
COMMENT ON COLUMN t_qa_user_token.deleted IS '逻辑删除：0-有效 1-已删除';

-- 分享记录表（转发会话给指定用户）
CREATE TABLE IF NOT EXISTS t_qa_share
(
    id              BIGSERIAL PRIMARY KEY,
    session_id      VARCHAR(128) NOT NULL,
    session_title   VARCHAR(256),
    from_user_id    VARCHAR(64)  NOT NULL,
    to_user_id      VARCHAR(64)  NOT NULL,
    deleted         INT          NOT NULL DEFAULT 0,
    creator         VARCHAR(64),
    create_time     TIMESTAMP,
    updater         VARCHAR(64),
    update_time     TIMESTAMP
);

COMMENT ON TABLE t_qa_share IS '会话分享记录';
COMMENT ON COLUMN t_qa_share.session_id IS 'LazyCraft 会话 ID';
COMMENT ON COLUMN t_qa_share.session_title IS '会话标题（快照，避免每次查 LazyCraft）';
COMMENT ON COLUMN t_qa_share.from_user_id IS '转发方用户 ID';
COMMENT ON COLUMN t_qa_share.to_user_id IS '接收方用户 ID';
COMMENT ON COLUMN t_qa_share.deleted IS '逻辑删除：0-有效 1-已删除';

-- 问题反馈表（用户对 AI 回答提交存疑/错误反馈）
CREATE TABLE IF NOT EXISTS t_qa_issue_report
(
    id             BIGSERIAL PRIMARY KEY,
    session_id     VARCHAR(128) NOT NULL,
    speak_id       BIGINT       NOT NULL,
    doubt_issue    TEXT,
    wrong_answer   TEXT,
    correct_answer TEXT,
    user_id        VARCHAR(64)  NOT NULL,
    deleted        INT          NOT NULL DEFAULT 0,
    creator        VARCHAR(64),
    create_time    TIMESTAMP,
    updater        VARCHAR(64),
    update_time    TIMESTAMP
);

COMMENT ON TABLE t_qa_issue_report IS 'AI 回答问题反馈记录';
COMMENT ON COLUMN t_qa_issue_report.session_id IS '所属会话 ID';
COMMENT ON COLUMN t_qa_issue_report.speak_id IS '被反馈的消息 ID（LazyCraft speak_id）';
COMMENT ON COLUMN t_qa_issue_report.doubt_issue IS '反馈存疑：存疑问题描述';
COMMENT ON COLUMN t_qa_issue_report.wrong_answer IS '反馈错误：错误答案描述';
COMMENT ON COLUMN t_qa_issue_report.correct_answer IS '反馈错误：正确答案描述';
COMMENT ON COLUMN t_qa_issue_report.user_id IS '提交反馈的用户 ID';
COMMENT ON COLUMN t_qa_issue_report.deleted IS '逻辑删除：0-有效 1-已删除';
