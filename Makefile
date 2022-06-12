all: build

.PHONY: all build build_code clean serve validate deploy

build:
	hugo --minify

build_code:
	code/gradlew -p code --console verbose build

clean:
	rm -rf public
	rm -rf code/build code/.gradle

serve:
	hugo server -D

validate:
	hugo check