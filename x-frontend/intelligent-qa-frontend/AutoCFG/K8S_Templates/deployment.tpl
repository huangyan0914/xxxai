---
# Deployment
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
  #namespace: demotestns
  labels:
    app: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
spec:
  selector:
    matchLabels:
      app: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
  replicas: 1
  template:
    metadata:
      labels:
        app: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
    spec:
      terminationGracePeriodSeconds: 30
      imagePullSecrets:
        - name: harborsecret
      nodeSelector:
        env-role: ${NODEENVROLE}
      containers:
      - name: ${ProgramName}-${ProjectName}-${GIT_BRANCH}
        image: ${imgrepoAddr}/${ProgramName}/${ProjectName}:${GIT_BRANCH}-${BUILD_VERSION}-${GIT_COMMIT_ID}
        imagePullPolicy: IfNotPresent
        ports:
        - containerPort: ${ContainerPort}
          name: web
          protocol: TCP
