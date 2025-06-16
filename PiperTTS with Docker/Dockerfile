FROM python:3.9-slim

WORKDIR /app

# 依存パッケージのインストール
RUN apt-get update && apt-get install -y \
    git \
    wget \
    espeak-ng-data \
    libespeak-ng1 \
    && rm -rf /var/lib/apt/lists/*

# Piper を公式 GitHub からクローン
RUN git clone https://github.com/rhasspy/piper.git

# Python ライブラリのインストール
WORKDIR /app/piper/src/python_run
RUN pip install --upgrade pip
RUN pip install -e .
RUN pip install -r requirements.txt
RUN pip install -r requirements_http.txt

# モデルファイルのダウンロード
WORKDIR /app/piper
RUN mkdir -p models && \
    wget -O models/en_US-hfc_female-medium.onnx "https://huggingface.co/rhasspy/piper-voices/resolve/v1.0.0/en/en_US/hfc_female/medium/en_US-hfc_female-medium.onnx?download=true" --user-agent="Mozilla/5.0" && \
    wget -O models/en_US-hfc_female-medium.onnx.json "https://huggingface.co/rhasspy/piper-voices/resolve/v1.0.0/en/en_US/hfc_female/medium/en_US-hfc_female-medium.onnx.json?download=true" --user-agent="Mozilla/5.0"

# サーバ起動（モデルパスを指定）
CMD ["python3", "-m", "piper.http_server", \
     "--model", "/app/piper/models/en_US-hfc_female-medium.onnx", \
     "--config", "/app/piper/models/en_US-hfc_female-medium.onnx.json"]
