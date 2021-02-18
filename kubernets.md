#Step1:- Create docker image.
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker image build -t code-challenge .
[+] Building 0.1s (8/8) FINISHED																
 => [internal] load build definition from Dockerfile                                             0.0s
 => => transferring dockerfile: 349B                                                             0.0s
 => [internal] load .dockerignore                                                                0.0s
 => => transferring context: 2B                                                                  0.0s
 => [internal] load metadata for docker.io/library/openjdk:8-jre-alpine3.9                       0.0s
 => [internal] load build context                                                                0.0s
 => => transferring context: 565B                                                                0.0s
 => [1/3] FROM docker.io/library/openjdk:8-jre-alpine3.9                                         0.0s
 => CACHED [2/3] COPY target/code-challenge-0.0.1-SNAPSHOT.jar /code-challenge.jar               0.0s
 => [3/3] COPY codechallenge.yaml /codechallenge.yaml                                            0.0s
 => exporting to image                                                                           0.0s
 => => exporting layers                                                                          0.0s
 => => writing image sha256:e2ecee38843d7b9fca71ad4949fa8a5b38d283d53e00d97d4541855e9591a25a     0.0s
 => => naming to docker.io/library/code-challenge                                                0.0s

## List the docker images
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker images -a
REPOSITORY                           TAG                         IMAGE ID       CREATED         SIZE
code-challenge                       latest                      4b3defefeb53   2 hours ago     101MB

#Step2:- Start your registry. To avoid going to Artifactory to pull the image 
docker run -d -p 5000:5000 --name registry registry:2

#Step3:- Tag the image so that it points to your registry 
docker image tag code-challenge localhost:5000/code-challenge
 
#Step4:- Push it 
docker push localhost:5000/code-challenge

##Local regitry got localhost:5000/code-challenge. you can delete localhost:5000/code-challenge and pull it to test
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker images -a
REPOSITORY                           TAG       IMAGE ID       CREATED             SIZE
code-challenge                       latest    56cb1812c8d2   About an hour ago   119MB
localhost:5000/code-challenge        latest    56cb1812c8d2   About an hour ago   119MB
registry                             2         678dfa38fcfa   2 months ago        26.2MB

#Step5:- Create deployment. use the image pushed into local registery
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl create deployment code-challenge --image=localhost:5000/code-challenge --port=8080 --output=yaml
deployment.apps/code-challenge created

##Check deployements
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get deployments
NAME             READY   UP-TO-DATE   AVAILABLE   AGE
code-challenge   1/1     1            1           23s

##Check pods
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get pods
NAME                              READY   STATUS    RESTARTS   AGE
code-challenge-74bcc9657c-dtwwm   1/1     Running   0          43s

#Step6:- Expose the deployement
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl expose deployment/code-challenge --type=LoadBalancer --port 8080
service/code-challenge exposed

## List services
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get services
NAME             TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
code-challenge   LoadBalancer   10.109.151.8   localhost     8080:31457/TCP   12s
kubernetes       ClusterIP      10.96.0.1      <none>        443/TCP          17m

##List Endpoints
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get endpoints
NAME             ENDPOINTS           AGE
code-challenge   10.1.0.35:8080      57s
kubernetes       192.168.65.3:6443   17m

#Step7:-Test the URL
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>curl http://localhost:8080
check cities connected ex: http://localhost:8080/connected?origin=Boston&destination=Newark


#---->Working with Non local image<-------------

#Create docker image

#Push docker image to Artifactory/Docker hup

#Create codechallenge.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: code-challenge
  labels:
    app: code-challenge
spec:
  replicas: 2
  selector:
    matchLabels:
      app: code-challenge
  template:
    metadata:
      labels:
        app: code-challenge
    spec:
      containers:
      - image: localhost:5000/code-challenge
        imagePullPolicy: Always
        name: code-challenge
        ports:
        - containerPort: 8080
        
#Apply deployement.yaml. If you chnage anhything in yaml file apply it again
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl apply -f codechallenge.yaml
deployment.apps/code-challenge created

#Get deployements
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get deployments
NAME             READY   UP-TO-DATE   AVAILABLE   AGE
code-challenge   0/2     2            0           60s

#Get list of pods. In YAML file we have two replicas
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get pods
NAME                              READY   STATUS             RESTARTS   AGE
code-challenge-6c9fc58955-6kfqc   0/1     ImagePullBackOff   0          2m46s
code-challenge-6c9fc58955-zptwd   0/1     ImagePullBackOff   0          2m46s

#Application is up and running. expose the application
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl expose deployment/code-challenge --type=LoadBalancer --port 8080
service/code-challenge exposed

#Check Endpoints
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get endpoints
NAME             ENDPOINTS           AGE
code-challenge   <IP>:<PORT>         56s

#Get Services
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get services
NAME             TYPE           CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
code-challenge   LoadBalancer   10.97.207.63   localhost     8080:31595/TCP   30s
kubernetes       ClusterIP      10.96.0.1      <none>        443/TCP          4d2h

#Test the URL
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>curl http://localhost:8080
check cities connected ex: http://localhost:8080/connected?origin=Boston&destination=Newark

#Get list of pods with --all-namespaces
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get pods --all-namespaces
NAMESPACE     NAME                                     READY   STATUS             RESTARTS   AGE
default       code-challenge-6c9fc58955-6kfqc          0/1     ImagePullBackOff   0          3m3s
default       code-challenge-6c9fc58955-zptwd          0/1     ImagePullBackOff   0          3m3s
kube-system   coredns-f9fd979d6-jnvk8                  1/1     Running            2          4d2h
kube-system   coredns-f9fd979d6-xjf8v                  1/1     Running            2          4d2h
kube-system   etcd-docker-desktop                      1/1     Running            2          4d2h
kube-system   kube-apiserver-docker-desktop            1/1     Running            2          4d2h
kube-system   kube-controller-manager-docker-desktop   1/1     Running            2          4d2h
kube-system   kube-proxy-mtq8l                         1/1     Running            2          4d2h
kube-system   kube-scheduler-docker-desktop            1/1     Running            3          4d2h
kube-system   storage-provisioner                      1/1     Running            4          4d2h
kube-system   vpnkit-controller                        1/1     Running            2          4d2h

#Get replicasets
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl get replicasets
NAME                        DESIRED   CURRENT   READY   AGE
code-challenge-6c9fc58955   2         2         0       6m59s

#Describe replicasets
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl describe replicasets
Name:           code-challenge-6c9fc58955
Namespace:      default
Selector:       app=code-challenge,pod-template-hash=6c9fc58955
Labels:         app=code-challenge
                pod-template-hash=6c9fc58955
Annotations:    deployment.kubernetes.io/desired-replicas: 2
                deployment.kubernetes.io/max-replicas: 3
                deployment.kubernetes.io/revision: 1
Controlled By:  Deployment/code-challenge
Replicas:       2 current / 2 desired
Pods Status:    0 Running / 2 Waiting / 0 Succeeded / 0 Failed
Pod Template:
  Labels:  app=code-challenge
           pod-template-hash=6c9fc58955
  Containers:
   code-challenge:
    Image:        code-challenge:latest
    Port:         8080/TCP
    Host Port:    0/TCP
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Events:
  Type    Reason            Age    From                   Message
  ----    ------            ----   ----                   -------
  Normal  SuccessfulCreate  7m41s  replicaset-controller  Created pod: code-challenge-6c9fc58955-6kfqc
  Normal  SuccessfulCreate  7m41s  replicaset-controller  Created pod: code-challenge-6c9fc58955-zptwd
  
#Describe service
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl describe services code-challenge
Name:                     code-challenge
Namespace:                default
Labels:                   app=code-challenge
Annotations:              <none>
Selector:                 app=code-challenge
Type:                     LoadBalancer
IP:                       10.103.153.93
LoadBalancer Ingress:     localhost
Port:                     <unset>  80/TCP
TargetPort:               80/TCP
NodePort:                 <unset>  31907/TCP
Endpoints:
Session Affinity:         None
External Traffic Policy:  Cluster
Events:                   <none>

#Clean up
##Delete service
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl delete service code-challenge
service "code-challenge" deleted

##Delete deployement
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl delete deployments code-challenge
deployment.apps "code-challenge" deleted

##Delete one pod
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl delete pod code-challenge
pod "code-challenge" deleted

##Delete all pods
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl delete pod --all
pod "code-challenge-864f84d667-hglfs" deleted
pod "code-challenge-864f84d667-vfhgt" deleted


#Before YAML modification. Decribe deployement code-challenge
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl describe deployments code-challenge
Name:                   code-challenge
Namespace:              default
CreationTimestamp:      Wed, 17 Feb 2021 14:57:07 -0500
Labels:                 app=code-challenge
Annotations:            deployment.kubernetes.io/revision: 1
Selector:               app=code-challenge
Replicas:               2 desired | 2 updated | 2 total | 0 available | 2 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=code-challenge
  Containers:
   code-challenge:
    Image:        code-challenge:latest
    Port:         80/TCP
    Host Port:    0/TCP
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      False   MinimumReplicasUnavailable
  Progressing    False   ProgressDeadlineExceeded
OldReplicaSets:  <none>
NewReplicaSet:   code-challenge-864f84d667 (2/2 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  17m   deployment-controller  Scaled up replica set code-challenge-864f84d667 to 2
  
  
#After YAML change and re apply of YAML
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl describe deployments code-challenge
Name:                   code-challenge
Namespace:              default
CreationTimestamp:      Wed, 17 Feb 2021 14:57:07 -0500
Labels:                 app=code-challenge
Annotations:            deployment.kubernetes.io/revision: 2
Selector:               app=code-challenge
Replicas:               2 desired | 1 updated | 3 total | 0 available | 3 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  app=code-challenge
  Containers:
   code-challenge:
    Image:        code-challenge:latest
    Port:         8080/TCP
    Host Port:    0/TCP
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      False   MinimumReplicasUnavailable
  Progressing    True    ReplicaSetUpdated
OldReplicaSets:  code-challenge-864f84d667 (2/2 replicas created)
NewReplicaSet:   code-challenge-6c9fc58955 (1/1 replicas created)
Events:
  Type    Reason             Age    From                   Message
  ----    ------             ----   ----                   -------
  Normal  ScalingReplicaSet  23m    deployment-controller  Scaled up replica set code-challenge-864f84d667 to 2
  Normal  ScalingReplicaSet  2m11s  deployment-controller  Scaled up replica set code-challenge-6c9fc58955 to 1  
  
#Get the logs of the application
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl logs code-challenge

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.3.4.RELEASE)

.....
............
←[30m2021-02-17 20:03:03,576←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 85]←[0;39m: Start loading city resource
←[30m2021-02-17 20:03:03,577←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 93]←[0;39m: Boston, New York
←[30m2021-02-17 20:03:03,577←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 93]←[0;39m: Philadelphia, Newark
←[30m2021-02-17 20:03:03,577←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 93]←[0;39m: Newark, Boston
←[30m2021-02-17 20:03:03,577←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 93]←[0;39m: Trenton, Albany
←[30m2021-02-17 20:03:03,578←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 93]←[0;39m: Fort Worth, Dallas
←[30m2021-02-17 20:03:03,578←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 93]←[0;39m: Dallas, Austin
←[30m2021-02-17 20:03:03,578←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 93]←[0;39m: Austin, San Antonio
←[30m2021-02-17 20:03:03,578←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 93]←[0;39m: San Antonio, Houston
←[30m2021-02-17 20:03:03,578←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 120]←[0;39m: {New York=[Boston], San Antonio=[Austin, Houston], Newark=[Philadelphia, Boston], Trenton=[Albany], Fort Worth=[Dallas], Austin=[San Antonio, Dallas], Dallas=[Fort Worth, Austin], Philadelphia=[Newark], Boston=[New York, Newark], Albany=[Trenton], Houston=[San Antonio]}
←[30m2021-02-17 20:03:03,579←[0;39m ←[34mINFO ←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.resource.CityConnector 123]←[0;39m: Loading city resource success
←[30m2021-02-17 20:03:03,587←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.configuration.SearchStrategy 41]←[0;39m: Use seatchStratagy------------------------->simpleSearch
←[30m2021-02-17 20:03:03,588←[0;39m ←[39mDEBUG←[0;39m [←[34mmain←[0;39m] ←[33m[com.mastercard.configuration.SearchStrategy 49]←[0;39m: Use SimpleSearch
←[30m2021-02-17 20:03:03,673←[0;39m ←[34mINFO ←[0;39m [←[34mmain←[0;39m] ←[33m[org.springframework.scheduling.concurrent.ExecutorConfigurationSupport 181]←[0;39m: Initializing ExecutorService 'applicationTaskExecutor'
←[30m2021-02-17 20:03:03,787←[0;39m ←[34mINFO ←[0;39m [←[34mmain←[0;39m] ←[33m[org.apache.juli.logging.DirectJDKLog 173]←[0;39m: Starting ProtocolHandler ["http-nio-8080"]
←[30m2021-02-17 20:03:03,802←[0;39m ←[34mINFO ←[0;39m [←[34mmain←[0;39m] ←[33m[org.springframework.boot.web.embedded.tomcat.TomcatWebServer 220]←[0;39m: Tomcat started on port(s): 8080 (http) with context path ''
←[30m2021-02-17 20:03:03,810←[0;39m ←[34mINFO ←[0;39m [←[34mmain←[0;39m] ←[33m[org.springframework.boot.StartupInfoLogger 61]←[0;39m: Started CodeChallengeApplication in 1.443 seconds (JVM running for 1.857)



#Use the docker image that was created. Refer docker.md
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>docker image list
REPOSITORY                           TAG                                                     IMAGE ID       CREATED          SIZE
code-challenge                       latest                                                  4b3defefeb53   11 minutes ago   101MB

#Run the doceker image in kubernetes
C:\Tutorial\Workspace\KairosTech_code_challenge\code-challenge>kubectl run code-challenge --image=code-challenge --image-pull-policy=Never --port=8080
pod/code-challenge created