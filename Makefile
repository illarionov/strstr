all: build

.PHONY: all build clean serve validate deploy

build:
	hugo --minify

clean:
	rm -rf public

serve:
	hugo server -D

validate:
	hugo check

deploy: build
	rclone sync --interactive public/ yandex_s3://strstr.0xdc.ru
