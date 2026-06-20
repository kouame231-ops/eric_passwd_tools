FROM debian:bookworm-slim

RUN apt-get update && \
    apt-get install -y --no-install-recommends cracklib-runtime && \
    rm -rf /var/lib/apt/lists/*

CMD ["tail", "-f", "/dev/null"]