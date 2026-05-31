apiVersion: v1
kind: Service
metadata:
  name: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
  #namespace: demotestns
  labels:
    app: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
spec:
  selector:
    app: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
  type: NodePort
  ports:
  - name: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
    port: ${ContainerPort}
    targetPort: web
    #nodePort: 30003
