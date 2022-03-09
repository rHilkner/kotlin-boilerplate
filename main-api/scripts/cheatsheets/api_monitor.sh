### WINDOWS
# Start prometheus
cd ..\api-monitor\prometheus-2.33.0.windows-amd64
.\prometheus.exe --config.file=..\..\main-api\src\main\resources\prometheus.yml

# Start grafana
cd ..\api-monitor\grafana-8.3.4\bin
.\grafana-server.exe

### LINUX
# TO-DO
