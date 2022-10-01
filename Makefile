all: build

.PHONY: all build build_code clean serve validate deploy validate_html

BUILD_LINT_DIR = "build/public"

build:
	hugo --minify

build_code:
	code/gradlew -p code --console verbose build

clean:
	rm -rf public
	rm -rf build
	rm -rf code/build code/.gradle

serve:
	hugo server -D

validate: validate_html

$(BUILD_LINT_DIR):
	hugo --destination $(BUILD_LINT_DIR)

validate_html: $(BUILD_LINT_DIR)
	@html5validator --root $(BUILD_LINT_DIR) \
		--ignore-re '(yandex_.+)|((Duplicate|of) ID \"TableOfContents\")' \
		--show-warnings \
		--also-check-svg
