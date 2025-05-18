from socket import socket

ADDRESS = ("192.168.0.129", 5000)

with socket() as client:
    client.connect(ADDRESS)
    name = input("あなたの名前: ")
    client.send(name.encode())

    print("チャット開始！'exit' と入力すると終了します。")
    while True:
        msg = input("> ")
        client.send(msg.encode())
        if msg.lower() == "exit":
            break
