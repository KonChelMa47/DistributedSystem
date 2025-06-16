# 📢 Piper TTS - Docker版 高速音声合成サーバー

このリポジトリでは、[Piper](https://github.com/rhasspy/piper) の音声合成エンジンを **Dockerで簡単に動かす** 構成を提供します。  
モデルのダウンロードからサーバー起動まで、すべて自動化されています。

---

## 🧠 Piperとは？

Piper は、軽量で高速・高品質な Text-to-Speech (TTS) エンジンです。  
リアルタイムでの音声合成が可能で、ONNXモデル形式によりマルチプラットフォームで動作します。

---

## 🚀 クイックスタート（3ステップ）

### 1. このリポジトリをクローン

```bash
git clone https://github.com/yourname/piper-docker.git
cd piper-docker
```

### 2. Dockerイメージをビルド

```bash
docker build -t piper-auto .
```

> ビルド中に英語モデル（en_US-hfc_female-medium）が自動的にダウンロードされます。

### 3. サーバーを起動

```bash
docker run --rm -it -p 5000:5000 piper-auto
```

---

## 🔊 サーバーを使って音声を生成

起動後は以下のようにHTTP POSTで音声合成ができます。

```bash
curl -G --data-urlencode 'text=This is a test.' -o test.wav 'localhost:5000'
```

---

## 📁 構成ファイルとモデルについて

本構成では以下のモデルファイルを使用：

| ファイル名                            | 説明                 |
|--------------------------------------|----------------------|
| `en_US-hfc_female-medium.onnx`       | 音声合成モデル本体    |
| `en_US-hfc_female-medium.onnx.json`  | モデル設定ファイル    |

ダウンロード元：
- https://huggingface.co/rhasspy/piper-voices/tree/v1.0.0

他言語・他話者モデルに切り替えたい場合は、`Dockerfile` 内のURLを変更してください。

---

## 🛠 Dockerfileの特徴

- Python 3.9 slim ベースで軽量
- モデルはビルド時に Hugging Face から取得
- espeak-ngライブラリを含み、音素化も可能
- `piper.http_server` による HTTP API を提供

---

## 🌐 ポート情報

| ホスト側 | コンテナ側 | 用途             |
|----------|------------|------------------|
| 5000     | 5000       | PiperのHTTP API |

---

## 📌 依存環境

- Docker（バージョン 20.10+ 推奨）
- インターネット接続（モデル取得のため）

---

## 🧩 拡張例

- [ ] 日本語モデルへの切り替え
- [ ] FlaskでのUI構築
- [ ] Whisper等と組み合わせた音声対話システム

ご希望があれば、これらの構築もサポートできます！

---

## 📝 ライセンス

この構成はMITライセンスに準拠しています。  
Piper本体およびモデルは [rhasspy/piper](https://github.com/rhasspy/piper) のライセンスに従います。
