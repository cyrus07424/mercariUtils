#!/bin/sh

# カレントディレクトリを移動
cd `dirname $0`

# pull
git pull

# ビルド
mvn -B package --file pom.xml

# 依存関係をインストール
mvn exec:java -e -Dexec.mainClass="com.microsoft.playwright.CLI" -Dexec.args="install-deps"

# 出品中の商品を一括で値下げ処理を実行
mvn exec:java -Dexec.mainClass="mains.DecreaseAllItemPrice"