from socket import socket
from threading import Thread

clients = []

def handle_client(sock, addr):
    name = sock.recv(1024).decode('utf-8').strip()
    print(f"[JOIN] {name} が参加しました")
    broadcast(f"{name} joined the chat!")

    clients.append((sock, name))
    try:
        while True:
            msg = sock.recv(1024).decode('utf-8').strip()
            if not msg or msg.lower().endswith("exit"):
                break
            print(f"[RECV from {name}] {msg}")
            broadcast(msg)
    except Exception as e:
        print(f"[ERROR] {e}")
    finally:
        print(f"[LEAVE] {name} が退出しました")
        clients.remove((sock, name))
        broadcast(f"{name} has left the chat.")
        sock.close()

def broadcast(message):
    print(f"[BROADCAST] {message}")
    for client, _ in clients:
        try:
            client.sendall((message + "\n").encode('utf-8'))  # 全員に送信（自分も含む）
        except:
            pass

def main():
    ADDRESS = ("0.0.0.0", 5000)
    with socket() as server:
        server.bind(ADDRESS)
        server.listen()
        print(f"[START] サーバー起動中: {ADDRESS[0]}:{ADDRESS[1]}")
        while True:
            client_sock, addr = server.accept()
            Thread(target=handle_client, args=(client_sock, addr), daemon=True).start()

if __name__ == "__main__":
    main()
