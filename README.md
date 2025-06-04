
The standard project template for Spring Boot. This is the main part of the news feed system with basic functions.

Technical features:
========================
- Spring Boot (https://spring.io/projects/spring-boot) - the main framework
- PostgreSQL (https://www.postgresql.org/) – the main relational database
- Redis (https://redis.io/) - message cache and queue via pub/sub
- Kafka (https://kafka.apache.org/) - event streaming platform - center of event-driven architecture
- Liquibase (https://www.liquibase.org/) – DB schema migration
- Amazon S3 (https://aws.amazon.com/ru/s3/) - object storage service
- MapStruct, ModelMapper - map data between differently structured objects (Java Beans)
- JUNIT5 (https://junit.org/junit5/} - testing framework
- Gradle (https://gradle.org/) - the application's build system
- Swagger - to create REST API docs for development and documentation,
- Docker (https://www.docker.com/) - build, share, and run container applications

Scenario - main features
c
  Each <feed-news-post> service contains three parts for working with data: DB, Redis, Kafka.
  The next operations are performed in this case:
  -publish post; -update post;  -create comment; -update comment; -like post; -like comment; post viewed

  The situation of receiving a news feed for a user.
  The application first searches for data in the cache and get a limited list of posts,
  that build his feed, using the user id. If it is missing, the data is taken directly from
  the DB and pulls it into the cache, and then gives it to the client.

  When a post or comment is published (each operation is saved in the DB), they are also cached in Redis
  along with their author for quick access. This action must be performed for all <feed-news-post> services.
  To do this is used Outbox Design Pattern and Apache Kafka.
   - first the events are persisted in the DB (event entity) as part of the transaction.
   - a scheduled job then publishes the messages about events to the Kafka message broker system at predefined time intervals.
  The Kafka consumer accepts the event and caches it for the author and for all his subscribers in FeedNews.
  Kafka producer is configured to use ACK for successful event processing only after all subscribers have been successfully processed.
  So, the caches are updated every time the data in the DB is updated.

  Redis is configured to use Spring transaction management.
  The posts are not duplicated and arranged in chronological order. If the server crashes and the same event is re-processed,
  the posts that were added during the first unsuccessful event processing will not be duplicated in the FeedNews.

  Similar to creating events for posts and comments, events are created for viewing and liking posts and comments.

  If the FeedNews cache is restarted (redis cluster), the original data will be lost. Accordingly, the FeedNews will work slowly.
  Therefore, it needs to be heated. The data will be taken from the DB for the last N days.
  to fill in FeedNews, as well as the Users and the Posts caches with comments, likes and view counts.

  Tmplementing idempotence in Spring microservices.
  To make sure that competitive events are processed correctly is used "distributed lock" with Redisson.
  In distributed systems where multiple instances of a service might be running,
  this mechanism allows to maintain the integrity and consistency of Redis data
  ensuring that only one instance processes a given operation.

  In order for posts to work with hashtags and upload files, they are used with custom annotations.

  The number of user's FeedNews and comments on posts is limited and can be changed in the external configuration file
  as well as other system parameters.




