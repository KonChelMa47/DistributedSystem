from socket import socket
from threading import Thread
import time

def loop():
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\n[STOP] Ctrl+Cでサーバーを終了します。")
        exit(0)

def handle_client(sock, addr):
    name = sock.recv(1024).decode().strip()
    print(f"[JOIN] {name} が参加しました")

    while True:
        msg = sock.recv(1024).decode().strip()
        if not msg or msg.lower() == "exit":
            print(f"[LEAVE] {name} が退出しました")
            break
        print(f"[{name}] {msg}")

    sock.close()

def main():
    ADDRESS = ("192.168.0.129", 5000)
    with socket() as server:
        server.bind(ADDRESS)
        server.listen()
        print(f"[START] サーバー起動中: {ADDRESS[0]}:{ADDRESS[1]}\n")

        while True:
            client_sock, addr = server.accept()
            Thread(target=handle_client, args=(client_sock, addr), daemon=True).start()

Thread(target=main, daemon=True).start()
loop()
