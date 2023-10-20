docker build -t websocket . 

docker run -itd -p 8080:8080 --name websocket_container websocket
