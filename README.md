# My  Todo App (Spring Boot✖️Thymeleaf）

# 概要

本アプリは Spring Boot / Spring Security / Spring Data JPA を中心に構築し、
設計〜実装まで一貫して自分で進めました。
実装方法に悩んだ部分やベストプラクティスの確認には
ChatGPT や Codex を 補助ツール として活用しつつ、
コードの理解・リファクタリングは必ず自分で行っています。

現時点でも、Java文法・テスト戦略・Spring Security・外部API連携など
一部に理解を深めている途中の領域がありますが、
正確に説明できるよう継続的に改善しています。
面談では「なぜこの設計にしたのか」「どこに課題を感じているのか」を
正直に自分の言葉で説明できればと思っています


# 制作理由

Spring Boot を本格的に学び始めたタイミングで、「就職活動で自信を持って見せられる作品を作りたい」という思いから、本アプリの制作を始めました。

当初は具体的にどの機能を作るべきかわからない状態でしたが、Todo アプリであれば CRUD の基礎だけでなく、
カテゴリ管理や検索、認証、API 連携など、実務に近い流れを自然に学べると考えて選びました。

実装を進める中で、
- カテゴリ機能  
- ユーザー認証  
- REST API（Swagger）  
- 外部 API 連携（天気 API）  
- ページネーション  
など、必要性が出てきた機能をひとつずつ追加することで、
Spring Boot を用いた Web アプリ開発の流れを「作りながら理解する」ことができました。

最終的には、就職活動の場でコードや設計の意図を説明できるポートフォリオとして仕上げることを目的としています。

# 特徴

- ユーザー認証（ログイン / 新規登録）
- Todo CRUD（作成・更新・削除・一覧・詳細）
- カテゴリ管理
- キーワード検索
- ページネーション
- ステータス変更
- 天気API(Open-Meteo)との連携
- REST API 提供（Swagger UI 対応）

# 機能一覧

Todo 機能
- タイトル・詳細・カテゴリ・期限・ステータスの設定
- 並び替え（期限が近い順 / 遠い順）
- 完了チェック
- カテゴリ別フィルター

ユーザー機能
- Spring Security による認証
- 自分の Todo のみ閲覧・編集可能

カテゴリ機能
- 一覧 / 作成 / 編集 / 削除

天気API機能
- 現在地（緯度・経度）からリアルタイムの天気情報を取得

# 画面イメージ

<img width="1453" height="838" alt="スクリーンショット 2025-11-16 15 29 26" src="https://github.com/user-attachments/assets/8753f5a6-28bf-4727-bd87-95e08163c19e" />
<img width="1459" height="869" alt="スクリーンショット 2025-11-16 15 29 56" src="https://github.com/user-attachments/assets/20e6f8c4-c435-4154-a0a1-274cd58855db" />
<img width="1444" height="791" alt="スクリーンショット 2025-11-16 15 30 39" src="https://github.com/user-attachments/assets/4f2115d1-b6c5-4bbb-844f-c245cd8f4e14" />
<img width="1455" height="764" alt="スクリーンショット 2025-11-16 15 30 14" src="https://github.com/user-attachments/assets/3eceb2fb-1ab8-4197-a468-10c312306482" />
<img width="1467" height="858" alt="スクリーンショット 2025-11-16 15 31 08" src="https://github.com/user-attachments/assets/7e266c69-c313-4826-9c44-bf9883722331" />

# データ構造
本アプリは、ユーザーごとに独立したカテゴリを管理できるよう
「ユーザー ー カテゴリ ー Todo」 の3エンティティで構成しています。

ER 図は以下の通りです：
erDiagram

    USER {
        BIGINT id PK
        VARCHAR username
        VARCHAR password
        VARCHAR role
    }

    CATEGORY {
        BIGINT id PK
        VARCHAR name
        BIGINT user_id FK
    }

    TODO {
        BIGINT id PK
        VARCHAR title
        TEXT description
        DATE due_date
        VARCHAR status
        BIGINT user_id FK
        BIGINT category_id FK
    }

    USER ||--o{ CATEGORY : "1対多（ユーザー別カテゴリ）"
    USER ||--o{ TODO : "1対多（Todo所有者）"
    CATEGORY ||--o{ TODO : "1対多（カテゴリに属するTodo）"

## User（ユーザー）

ユーザー情報を管理するエンティティで、
アプリにログインして操作するアカウントを表します。

1人のユーザーは複数のカテゴリを所有できます

また、複数のTodoを登録できます
（1対多の関係）

## Category（カテゴリ）

ユーザーが自主的に作成する分類ラベルです。
カテゴリは “ユーザーごとに独立” しており、
他ユーザーのカテゴリが見えたり使用されることはありません。

1つのカテゴリに対し、複数のTodoが紐づきます（1対多）

## Todo（タスク）

実際のタスクデータを保持するエンティティです。
Todoは必ず以下の2つに属します：

登録した User

選択した Category

Todo はユーザーのタスクを管理するための中心的なエンティティです。


# APIドキュメント（API は全て /api 以下で提供しており、Swagger UI にて閲覧可能です。）

<img width="1424" height="377" alt="スクリーンショット 2025-11-16 15 27 24" src="https://github.com/user-attachments/assets/032efdd6-7e44-4672-b085-109664538e76" />
<img width="1441" height="366" alt="スクリーンショット 2025-11-16 15 27 37" src="https://github.com/user-attachments/assets/6c40207c-270e-4f3b-a1b8-81b5fb82446e" />
<img width="1454" height="361" alt="スクリーンショット 2025-11-16 15 28 06" src="https://github.com/user-attachments/assets/67a01e9c-f3d8-43d6-a129-23d22eb04ac5" />
<img width="1450" height="816" alt="スクリーンショット 2025-11-16 15 28 37" src="https://github.com/user-attachments/assets/35fdc7fa-4b44-4a42-9188-ed512b4f3636" />


# 使用技術
## Backend
- Java 17
- Spring Boot 3.x
- Spring MVC / Spring Security / Spring Data JPA

## Frontend
- Thymeleaf
- Tailwind CSS

## Database
- MySQL / H2(開発時)

## その他
- Swagger UI (OpenAPI)
- Lombok


# テスト構成の概要
本アプリでは、品質向上と回 regressions を防ぐために
以下の 3 層構造でテストを実施しています。

Unit Test（単体テスト）
Service 層を Mockito でモック化し、ビジネスロジック・バリデーション・
ユーザー所有権チェックなどの挙動を検証。

WebMvcTest（コントローラ単体テスト）
Controller を対象とし、
リクエスト/レスポンス・Validation・画面遷移・JSON構造 を確認。

Integration Test（統合テスト）
Spring Boot を実際に起動し、
H2 メモリDB上で CRUD・認証・API の一連の動作を検証。

# アーキテクチャ

本アプリは、保守性・拡張性を考慮してレイヤードアーキテクチャを採用しています。

### ● プレゼンテーション層
ユーザーからの HTTP リクエストを受け取り、レスポンスを返す層です。
Controller を中心に、入力値の受け取り、画面表示、ルーティングなどを担当します。
ビジネスロジックは持たず、Service 層に処理を委譲することで責務を分離しています。

### ● ビジネスロジック層
アプリケーション固有の業務処理を担当する中心的な層です。
Todo の作成・更新、ユーザー紐付け、カテゴリ判定など、
Controller から受け取った要求をもとに必要な処理を行い、
Repository や他サービスを組み合わせて結果を返します。

### ● 永続化層
データベースとの通信を担当する層です。
Spring Data JPA を使用し、Entity の保存・検索・更新を行います。
Repository にはビジネスロジックを含めず、データアクセスに責務を限定しています。

### ● ドメイン層
Todo、User、Category などアプリケーションの主要なデータ構造を表現します。
DB テーブル構造に近い形でモデル化され、リレーションや制約を定義します。

### ● DTO（データ転送オブジェクト）
API のリクエスト/レスポンス専用のデータ形式です。
Entity を外部に直接公開せず、必要な情報のみをやり取りするための仕組みとして
TodoRequest / TodoResponse / TodoMapper を使用しています。


# 今後のステップ
Spring Security（認証・認可）の理解深化

DTO / Mapper の整理と Controller の責務分離

テストケースの追加（エッジケース／例外系／セキュリティテスト）

外部API連携の改善（例：キャッシュ、レート制限ハンドリングなど）

Tailwind + Thymeleaf の UI 改善

ログ出力・例外ハンドラーの整備

# 今後の拡張予定
OAuth2 / SNSログイン対応
Docker 化 & 本番環境デプロイ
Redis を用いたキャッシュ導入
GraphQL API 対応
React や Next.js とのフロント分離構成
通知機能（メール/Slack/LINE）
監査ログ・変更履歴機能


技術選定は「実務でよく使われる技術」「学習効果が高い技術」を軸に行いました。  
Spring Boot を中心に、認証、DB連携、API設計、画面生成まで一通りの開発フローを経験できる構成としています。


# 制作者
mikk-dmm

バックエンドエンジニア志望。
Spring Boot / Java / MySQL / Rails などを学習中。
