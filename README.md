# 分散システムチーム開発用リポジトリ

このリポジトリは、〇〇大学「分散システム」の授業において、チームで共同開発を行うためのプライベートリポジトリです。  
このドキュメントでは、Gitを使ったことがない方でも分かるように、**Gitのインストールから、コードの編集、アップロード（push）までの流れ**を丁寧に解説します。

---

## ✅ 事前準備：Gitを使えるようにする

### Gitのインストール

#### macOSの場合

ターミナルを開いて以下のコマンドを実行：

```bash
xcode-select --install
```

#### Ubuntu/Linuxの場合

```bash
sudo apt update
sudo apt install git
```

#### Windowsの場合

[Git公式サイト](https://git-scm.com/)から「Windows用Git」をダウンロードしてインストールしてください。

### インストール確認

以下のコマンドを実行して、バージョンが表示されればインストール完了です：

```bash
git --version
```

---

## 🚀 リポジトリを自分のPCにコピーする（clone）

1. ターミナルやコマンドプロンプトを開き、作業用のディレクトリに移動します。
2. 次のコマンドを実行して、リポジトリをコピー（clone）します：

```bash
git clone https://github.com/KonChelMa47/DistributedSystem.git
```

3. 以下のコマンドでそのフォルダに移動：

```bash
cd DistributedSystem
```

---

## 🌱 ブランチを作って自分の作業スペースを確保する

### ブランチとは？

ブランチは「作業用のコピー」のようなものです。`main` ブランチを直接いじらず、自分専用のブランチで作業しましょう。

### ブランチを作る

```bash
git checkout -b your-name-feature
```

例：

```bash
git checkout -b daniil-timer
```

---

## 💻 編集したら、変更を保存してGitHubにアップロードする（push）

### 1. ファイルを変更した後、その変更をステージ（追加）する

```bash
git add .
```

※「.」はすべての変更を対象にする意味です。

### 2. コミットする（変更の履歴を保存）

```bash
git commit -m "ここに変更内容を簡潔に書く"
```

例：

```bash
git commit -m "タイマーUIを追加"
```

### 3. GitHubにアップロードする（push）

```bash
git push origin your-name-feature
```

---

## 🔄 チームに変更を共有する（Pull Request）

1. GitHubのこのリポジトリページにアクセスします。
2. 画面上部に「Compare & pull request」ボタンが表示されたらクリックします。
3. 内容を確認して「Create pull request」ボタンを押します。

Pull Requestとは、**「自分のブランチの変更をmainに取り込んでほしい」という提案**です。チームでレビューしてからマージします。

---

## 📌 注意事項

- `main`ブランチでは直接作業しないでください。
- 必ずブランチを作成して作業し、Pull Requestで変更を提案してください。
- 作業中に他の人の変更を取り込む場合は、次のコマンドで最新の状態に更新できます：

```bash
git pull origin main
```

（必要に応じて自分のブランチにマージしてください）

---

## 📚 参考リンク

- [Git公式サイト](https://git-scm.com/)
- [サルでもわかるGit入門（日本語）](https://backlog.com/ja/git-tutorial/)
- [GitHub Flow（チーム開発の流れ）](https://docs.github.com/ja/get-started/quickstart/github-flow)

---

## 🤝 チームでの協力を大切に！

わからないことがあれば、Slackやチャットで気軽に相談してください。  
みんなで協力して良いプロジェクトにしていきましょう！
