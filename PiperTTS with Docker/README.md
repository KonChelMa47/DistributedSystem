# 📢 Piper TTS - Docker版 高速音声合成サーバー（自動クローン・自動モデルDL対応）

この構成では、[Piper](https://github.com/rhasspy/piper) の音声合成エンジンを **Dockerだけで完全自動構築・起動** できます。  
ソースコードやモデルのクローン・取得も Dockerfile 内に含まれており、追加作業は一切不要です。

---

## 🚀 クイックスタート

### 1. Docker イメージをビルド

```bash
docker build -t piper-auto .
```

> 初回ビルド時に Piper のクローンおよび音声モデルのダウンロードが自動で行われます。

### 2. サーバーを起動

```bash
docker run --rm -it -p 5000:5000 piper-auto
```

---

## 🔊 音声を生成する

サーバー起動後、以下のように HTTP 経由で音声合成ができます：

```bash
curl -G --data-urlencode 'text=This is a test.' -o test.wav 'localhost:5000'
```

---

## 📁 使用モデル

この構成では以下のモデルファイルが自動ダウンロードされます：

| ファイル名                            | 説明                 |
|--------------------------------------|----------------------|
| `en_US-hfc_female-medium.onnx`       | 音声合成モデル本体    |
| `en_US-hfc_female-medium.onnx.json`  | モデル設定ファイル    |

ダウンロード元（Hugging Face）:
- https://huggingface.co/rhasspy/piper-voices/tree/v1.0.0

---

## 🛠 Dockerfile の特徴

- Piper を GitHub から自動クローン
- モデルも Hugging Face から自動ダウンロード
- Python 依存ライブラリや `espeak-ng` も自動セットアップ
- HTTPサーバを `--model` / `--config` 指定で起動

---

## 🌐 ポート情報

| ホスト側 | コンテナ側 | 用途             |
|----------|------------|------------------|
| 5000     | 5000       | Piper の HTTP API |

---

## 📌 前提条件

- Docker（20.10以降推奨）
- インターネット接続（ビルド時にモデル取得のため）

---

## 🧩 拡張アイデア

- [ ] 日本語モデルへの変更（モデルURLを書き換えるだけ）
- [ ] Whisper音声認識との連携による対話ボット化
- [ ] WebUIやLINEボットとの連携
