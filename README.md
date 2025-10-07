# Getting Started

Welcome to the Scheduler Project! This guide will help you understand the structure of the project and provide useful references to help you get started.

## Reference Documentation

For further reference, please consider the following sections:

- [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
- [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.6/maven-plugin)
- [Create an OCI image](https://docs.spring.io/spring-boot/3.5.6/maven-plugin/build-image.html)
- [Spring Data Redis (Access+Driver)](https://docs.spring.io/spring-boot/3.5.6/reference/data/nosql.html#data.nosql.redis)
- [Spring for Apache Kafka](https://docs.spring.io/spring-boot/3.5.6/reference/messaging/kafka.html)

## Guides

The following guides illustrate how to use some features concretely:

- [Messaging with Redis](https://spring.io/guides/gs/messaging-redis/)

## Maven Parent Overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM. While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent. To prevent this, the project POM contains empty overrides for these elements. If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## Project Structure

Below is an overview of the project structure:

```
src/main/kotlin/com/yourcompany/scheduler
├── config                     // Spring configuration classes (@Configuration)
│   ├── ExecutorConfig.kt      // Defines ScheduledExecutorService bean
│   ├── KafkaConfig.kt         // Kafka producer/consumer setup
│   ├── RedisConfig.kt         // RedisTemplate configuration (if needed beyond defaults)
│   └── WebConfig.kt           // General web/security config
│
├── domain                     // JPA Entities and Value Objects
│   ├── TaskSchedule.kt        // (Entity) The table holding tasks to be run
│   ├── Job.kt                 // (Entity) Static metadata about a recurring job
│   ├── TaskExecutionHistory.kt// (Entity) Records of all past executions
│   └── TaskPayload.kt         // (Data Class) The object sent over Kafka
│
├── repository                 // Spring Data JPA Repositories
│   ├── JobRepository.kt
│   ├── TaskScheduleRepository.kt // Contains the critical atomic @Query methods
│   └── TaskExecutionHistoryRepository.kt
│
├── service                    // Core Business Logic (Master and Worker roles)
│   ├── worker                 // Worker-specific logic (Polling, Dispatch)
│   │   ├── WorkerService.kt     // Runs the polling loop and dispatches tasks
│   │   └── SegmentService.kt    // Logic to determine this worker's assigned segments
│   │
│   ├── master                 // Master-specific logic (Recurrence, Maintenance)
│   │   ├── MasterService.kt     // Handles recurring job creation
│   │   └── MasterCleanupService.kt // Handles orphaned task cleanup (if needed)
│   │
│   ├── common                 // Logic used by both Master and Worker
│   │   └── SchedulerRoleService.kt // Determines role via Redis lock, starts Master/Worker threads
│   │
│   └── kafka                  // Kafka specific services
│       └── KafkaProducerService.kt // Wraps KafkaTemplate for type safety
│
├── runner                     // Entry point for background services
│   └── TaskRunner.kt          // @Component, @PostConstruct: Submits tasks to ExecutorService
│
└── SchedulerApplication.kt

```

## Run locally

```bash 
docker-compose -f docker-compose.yml -p job_scheduler up -d 
```

