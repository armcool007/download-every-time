# syntax=docker/dockerfile:1
FROM python:3.11-slim

# Install system deps
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Create user
RUN useradd -m appuser
WORKDIR /app

# Install Python deps first for caching
COPY dash_app/requirements.txt /tmp/requirements.txt
RUN pip install --no-cache-dir -r /tmp/requirements.txt

# Copy app
COPY dash_app /app

# Security: drop root
USER appuser

# Expose and run
ENV PORT=8050
EXPOSE 8050
CMD ["gunicorn", "app:server", "-b", "0.0.0.0:8050", "--workers", "2", "--threads", "4"]
