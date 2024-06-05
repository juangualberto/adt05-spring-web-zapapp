# Puesta en producción con Kubernetes


## Kubernetes Cheatsheet

Cheatsheet creada a partir de [este curso de introducción a kubernetes](https://github.com/iesgn/curso_kubernetes_cep).

### Instalación y autocompletado

Instalación:

```bash
curl -LO "https://storage.googleapis.com/kubernetes-release/release/$(curl -s https://storage.googleapis.com/kubernetes-release/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install kubectl /usr/local/bin/kubectl
```

Autocompletado:

```bash
echo 'source <(kubectl completion bash)' >>~/.bashrc
```

### Gestión de Pods

Acción | Comando
-------|--------
Creación de pod desde archivo | kubectl create -f pod.yaml
Borrar un pod | kubectl delete pod pod-name
Ver estado | kubectl get pod -o wide
Información detallada de un pod | kubectl describe pod pod-name
Editar y ver todos los atributos de un pod | kubectl edit pod pod-name
Ver los logs del pod | kubectl logs pod-name
Ejecutar comando en el pod | kubectl exec -it pod-name -- /bin/bash
NAT | kubectl port-forward pod-nginx PUERTO_ANFITRION:PUERTO_POD
Ver etiquetas | kubectl get pods --show-labels
Establecer etiqueta | kubectl label pods pod-name service=etiqueta_nueva --overwrite=true
Listar pods por etiquetas | kubectl get pods -l service=etiqueta_nueva
Listar pods con etiquetas como columnas | kubectl get pods -Lservice

Ejemplo de fichero pod.yaml

```yaml
apiVersion: v1
kind: Pod
metadata:
 name: pod-nginx
 labels:
   app: nginx
   service: web
spec:
 containers:
   - image: nginx:1.16
     name: contenedor-nginx
     imagePullPolicy: Always
```

## Despliegues: ReplicaSets / RS / Tolerancia a fallos y escalabilidad

Acción | Comando
-------|--------
Creación de un despliegue | kubectl create deployment nginx --image nginx
Creación de un despliegue a partir de fichero | kubectl apply -f nginx-deployment.yaml
Listado de despliegues | kubectl get deploy,rs,pod
Listado de todos los recursos | kubectl get all
Escalado de despliegues | kubectl scale deployment deployment-nginx --replicas=4
Reenvío de puertos de despliegues | kubectl port-forward deployment/deployment-nginx 8080:80
Ver los logs de un depliegue | kubectl logs deployment/deployment-nginx
Ver información detallada de un despliegue | kubectl describe deployment/deployment-nginx
Eliminar un despliegue | kubectl delete deployment deployment-nginx

Ejemplo de fichero de despliegue:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deployment-nginx
  labels:
    app: nginx
spec:
  revisionHistoryLimit: 2
  strategy:
    type: RollingUpdate
  replicas: 2
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - image: nginx
        name: contendor-nginx
        ports:
        - name: http
          containerPort: 80
```


\pagebreak

