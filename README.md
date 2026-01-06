# My Todo App（Spring Boot × Thymeleaf）
## 概要

Spring Boot を使って、認証・Todo管理・カテゴリ・検索・API 連携・デプロイまで
Webアプリ開発の流れを一通り経験することを目的に制作したポートフォリオです。

実装に迷った場面では ChatGPT ,Codex などの AI ツールを 補助的に活用しつつ、
コードの理解・設計判断・リファクタリングは 必ず自分で確認して進める 形で開発しました。
「なぜその実装にしたのか」を説明できるように意識しながら、
必要な技術を段階的に学び、継続的に改善しています。

### デモURL

https://todoapp-kx0z.onrender.com（今は開けません）

## 主な特徴

ログイン / 新規登録（Spring Security）

Todo CRUD（作成・編集・削除・一覧・詳細）

カテゴリ別管理

キーワード検索 / ソート

ページネーション

ステータス管理

天気 API（Open-Meteo）連携

REST API（Swagger UI 対応）

Docker 対応（開発環境）

Render（Docker Deploy + PostgreSQL）で本番運用

PC / モバイル対応（レスポンシブ）

## 画面イメージ（PC版）
<img width="1470" height="956" alt="スクリーンショット 2025-11-22 0 46 06" src="https://github.com/user-attachments/assets/f47c2e2c-c161-47ce-b601-b56e1f8eea5f" />
<img width="1470" height="956" alt="スクリーンショット 2025-11-22 1 08 33" src="https://github.com/user-attachments/assets/74bb037d-4c61-4480-98c5-795cf6f9c0e6" />
<img width="1470" height="956" alt="スクリーンショット 2025-11-22 1 01 51" src="https://github.com/user-attachments/assets/40ef1cdd-f2ae-4d95-bb02-36eed52ba701" />
<img width="1470" height="956" alt="スクリーンショット 2025-11-22 1 08 42" src="https://github.com/user-attachments/assets/07fa503a-05f4-4812-9ce8-2742c631c500" />
<img width="1470" height="956" alt="スクリーンショット 2025-11-22 1 08 59" src="https://github.com/user-attachments/assets/6ff3e790-8642-4880-9ceb-cf0349607751" />



### モバイル版
<img width="175" height="350" alt="スクリーンショット 2025-11-22 1 21 35 jpeg" src="https://github.com/user-attachments/assets/e63e8914-4f27-4b6a-9a3d-a086e95afa78" />

その他キャプチャ準備中


<details>
<summary><strong>アーキテクチャ / ER図 / 技術詳細</strong></summary>
    
# アーキテクチャ（レイヤード構成）

Presentation（Controller / View）

Service（ビジネスロジック）

Repository（Spring Data JPA）

Domain（User / Todo / Category）

DTO / Mapper（API用のデータ転送）

## ER 図
USER ||--o{ CATEGORY : 1対多
USER ||--o{ TODO : 1対多
CATEGORY ||--o{ TODO : 1対多

## 使用技術

Backend: Java17 / Spring Boot / Spring Security / JPA
Frontend: Thymeleaf / Tailwind CSS
DB: MySQL（開発） / PostgreSQL（本番）
その他: Swagger UI / Lombok / Docker / Render

</details>
<details>
<summary><strong>Docker / Render（本番環境）構成</strong></summary>
    
# 🐳Docker（開発環境）

開発環境では Docker Compose を利用し、
アプリ（Spring Boot）＋ MySQL をコンテナで統合しています。

docker compose up -d

http://localhost:8081


（compose.yml は /docker-compose.yml に配置）

## Render デプロイ（本番）

Render Web Service（Docker Deploy）

Render Managed PostgreSQL

環境変数で本番設定を注入

（DB_URL / DB_USERNAME / DB_PASSWORD / WEATHER_API_KEY）

本番プロファイルは SPRING_PROFILES_ACTIVE=prod を指定。

## PostgreSQL を採用した理由

Render が PostgreSQL を標準提供している

Spring Data JPA により MySQL → PostgreSQL の移行が容易

</details>
<details>
<summary><strong>テスト構成</strong></summary>
    
## Unit Test

Mockito を用いた Service 層のロジック検証

## WebMvcTest

Controller レイヤーの動作・バリデーション・レスポンス構造を確認

## Integration Test

Spring Boot + H2 による CRUD / 認証 / API の統合テスト

</details>
<details>
<summary><strong>セットアップ方法（ローカル）</strong></summary>
    
git clone https://github.com/xxxxx/todoapp

docker compose up -d

</details>
<details>
<summary><strong>今後の拡張</strong></summary>

OAuth2 / SNS ログイン

Redis キャッシュ

GraphQL API

React / Next.js 連携

監査ログ / 通知機能

</details>

### 制作者

mikk_dm

ソフトウェアエンジニア志望 / Java, Spring Bootを学習中

最終更新日（2026/1/7)
