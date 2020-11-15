# Elastic Stack 优化

## Elasticsearch

Node： 装有一个 ES 服务器的节点。
 Cluster： 有多个Node组成的集群
 Document： 一个可被搜素的基础信息单元
 Index： 拥有相似特征的文档的集合
 Type： 一个索引中可以定义一种或多种类型
 Filed： 是 ES 的最小单位，相当于数据的某一列
 Shards： 索引的分片，每一个分片就是一个 Shard
 Replicas： 索引的拷贝





```shell
cluster.name: elasticsearch
#配置es的集群名称，默认是elasticsearch，es会自动发现在同一网段下的es，如果在同一网段下有多个集群，就可以用这个属性来区分不同的集群。
node.name: "Franz Kafka"
#节点名，默认随机指定一个name列表中名字，该列表在es的jar包中config文件夹里name.txt文件中，其中有很多作者添加的有趣名字。
node.master: true
#指定该节点是否有资格被选举成为node，默认是true，es是默认集群中的第一台机器为master，如果这台机挂了就会重新选举master。
node.data: true
#指定该节点是否存储索引数据，默认为true。
index.number_of_shards: 5
#设置默认索引分片个数，默认为5片。
index.number_of_replicas: 1
#设置默认索引副本个数，默认为1个副本。
path.conf: /path/to/conf
#设置配置文件的存储路径，默认是es根目录下的config文件夹。
path.data: /path/to/data
#设置索引数据的存储路径，默认是es根目录下的data文件夹，可以设置多个存储路径，用逗号隔开，例：
#path.data: /path/to/data1,/path/to/data2
path.work: /path/to/work
#设置临时文件的存储路径，默认是es根目录下的work文件夹。
path.logs: /path/to/logs
#设置日志文件的存储路径，默认是es根目录下的logs文件夹
path.plugins: /path/to/plugins
#设置插件的存放路径，默认是es根目录下的plugins文件夹
bootstrap.mlockall: true
#设置为true来锁住内存。因为当jvm开始swapping时es的效率会降低，所以要保证它不swap，可以把#ES_MIN_MEM和ES_MAX_MEM两个环境变量设置成同一个值，并且保证机器有足够的内存分配给es。同时也要#允许elasticsearch的进程可以锁住内存，linux下可以通过`ulimit -l unlimited`命令。
network.bind_host: 192.168.0.1
#设置绑定的ip地址，可以是ipv4或ipv6的，默认为0.0.0.0。
network.publish_host: 192.168.0.1
#设置其它节点和该节点交互的ip地址，如果不设置它会自动判断，值必须是个真实的ip地址。
network.host: 192.168.0.1
#这个参数是用来同时设置bind_host和publish_host上面两个参数。
transport.tcp.port: 9300
#设置节点间交互的tcp端口，默认是9300。
transport.tcp.compress: true
#设置是否压缩tcp传输时的数据，默认为false，不压缩。
http.port: 9200
#设置对外服务的http端口，默认为9200。
http.max_content_length: 100mb
#设置内容的最大容量，默认100mb
http.enabled: false
#是否使用http协议对外提供服务，默认为true，开启。
gateway.type: local
#gateway的类型，默认为local即为本地文件系统，可以设置为本地文件系统，分布式文件系统，hadoop的#HDFS，和amazon的s3服务器，其它文件系统的设置方法下次再详细说。
gateway.recover_after_nodes: 1
#设置集群中N个节点启动时进行数据恢复，默认为1。
gateway.recover_after_time: 5m
#设置初始化数据恢复进程的超时时间，默认是5分钟。
gateway.expected_nodes: 2
#设置这个集群中节点的数量，默认为2，一旦这N个节点启动，就会立即进行数据恢复。
cluster.routing.allocation.node_initial_primaries_recoveries: 4
#初始化数据恢复时，并发恢复线程的个数，默认为4。
cluster.routing.allocation.node_concurrent_recoveries: 2
#添加删除节点或负载均衡时并发恢复线程的个数，默认为4。
indices.recovery.max_size_per_sec: 0
#设置数据恢复时限制的带宽，如入100mb，默认为0，即无限制。
indices.recovery.concurrent_streams: 5
#设置这个参数来限制从其它分片恢复数据时最大同时打开并发流的个数，默认为5。
discovery.zen.minimum_master_nodes: 1
#设置这个参数来保证集群中的节点可以知道其它N个有master资格的节点。默认为1，对于大的集群来说，可以设置大一点的值（2-4）# 通过配置大多数节点(节点总数/ 2 + 1)来防止脑裂
discovery.zen.ping.timeout: 3s
#设置集群中自动发现其它节点时ping连接超时时间，默认为3秒，对于比较差的网络环境可以高点的值来防止自动发现时出错。
discovery.zen.ping.multicast.enabled: false
#设置是否打开多播发现节点，默认是true。
discovery.zen.ping.unicast.hosts: ["host1", "host2:port", "host3[portX-portY]"]
#设置集群中master节点的初始列表，可以通过这些节点来自动发现新加入集群的节点。
```

```shell
cluster.name: lhy-application
node.name: lhy
bootstrap.memory_lock: false
network.host: 0.0.0.0
discovery.seed_hosts: ["0.0.0.0"]
cluster.initial_master_nodes: ["lhy"]
xpack.monitoring.collection.enabled: true 
```



```shell

[1]: max file descriptors [4096] for elasticsearch process is too low, increase to at least [65535]
[2]: memory locking requested for elasticsearch process but memory is not locked
[3]: max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
[4]: the default discovery settings are unsuitable for production use; at least one of [discovery.seed_hosts, discovery.seed_providers, cluster.initial_master_nodes] must be configured

[1]: max file descriptors [4096] for elasticsearch process is too low, increase to at least [65535]
# 查看当前值
ulimit -a
# 临时修改
ulimit -n 65535
# 永久修改
vim /etc/security/limits.conf
elasticsearch  -  nofile  65535

[2]: memory locking requested for elasticsearch process but memory is not locked
# 查看当前值
ulimit -l
# 临时修改
ulimit -n unlimited
# 永久修改
vim /etc/security/limits.conf
elasticsearch  -  memlock  unlimited

[3]: max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
# 查看当前值
sysctl vm.max_map_count
# 临时修改
sysctl -w vm.max_map_count=262144
# 永久修改
vim /etc/sysctl.conf
vm.max_map_count=262144
# 载入文件
sysctl -p
```





`由于每次搜索都是针对每个分片单线程执行的(虽然是并行查询多个分片)，因此分片大小会影响查询性能。由于您有大量未编制索引的数据，因此您可以使用比我们通常推荐的更大的碎片。为了保证集群中的恢复不会因为过大的分片而停滞，我们通常建议将分片控制在50 GB以下。如果您只在单个节点上运行，这自然就不那么重要了。`



摄取节点的一大优点是它允许非常简单的体系结构，其中Beats可以直接写入摄取节点管道。Elasticsearch集群中的每个节点都可以充当接收节点，这可以降低硬件占用空间并降低体系结构的复杂性，至少对于较小的用例是这样。

一旦数据量增长或处理变得更加复杂，导致集群中的CPU负载更高，通常建议改用专用摄取节点。此时将需要额外的硬件来托管专用接收节点或Logstash，硬件占用空间方面的任何差异将在很大程度上取决于使用情形。



(1)在输入方面，虽然Logstash支持更多的输入模式，但Ingest Node可以配合Beats甚至Logstash来解决不同数据源的输入问题。

(2)输出：作为Elasticsearch索引文档流程的一部分，除非重新构建源代码，否则无法将Inest Node导出到其他地方。因此，如果您需要将处理后的数据导出到其他地方，建议您使用Logstash。

(3)在数据缓冲方面，摄取节点可以访问Kafka等消息队列来解决问题。实际上，即使Logstash有队列机制，也会在Logstash之前添加Kafka，以更好地缓冲数据传输压力。

(4)在数据处理方面，IgnestNode支持Logstash最常见的场景。在使用Logstash之前，您可以使用IgnestNode来确定接收节点是否满足要求。如果IgnestNode不符合要求，则需要使用Logstash。

(5)Logstash在配置和使用方面优势明显，配置方式灵活，可视化监控和流水线管理，对复杂系统有很大帮助。

(6)性能和架构：作为Elasticsearch集群的一部分，Ingest在数据处理逻辑相对简单的情况下，简化了数据采集和处理架构。但是，当数据处理比较复杂时，摄取节点数据处理可能会影响节点的性能。在这种情况下，使用专用的摄取节点来解决问题。专用摄取节点的引入使Elasticsearch集群的结构变得复杂，这与摄取节点在简化数据收集和处理体系结构方面的优势背道而驰。Logstash是Elasticsearch之前的专用数据采集和处理模块。当出现性能瓶颈时，可以横向扩展Logstash，提高处理能力，架构更加清晰。

(7)摄取节点连接到Logstash。接收节点是Elasticsearch群集的一部分。Logstash位于Elasticsearch的前端。摄取节点可以与Logstash一起使用。InsterNode的一种用途是向数据添加时间戳，以便更精确地记录文档索引时间，这在耗时的计算中很有用。下图显示了InsterNode和Logstash使用的弹性堆栈架构。



选型小结：

1、两种方式各有利弊，建议小数据规模，建议使用Ingest节点。原因：架构模型简单，不需要额外的硬件设备支撑。

2、数据规模大之后，除了建议独立Ingest节点，同时建议架构中使用Logstash结合消息队列如Kafka的架构选型。

3、将Logstash和Ingest节点结合，也是架构选型参考方案之一。



BulkAPI

在单个API调用中执行多个索引或删除操作。这降低了开销，并且可以极大地提高索引速度。

```console
POST _bulk
{ "index" : { "_index" : "test", "_id" : "1" } }
{ "field1" : "value1" }
{ "delete" : { "_index" : "test", "_id" : "2" } }
{ "create" : { "_index" : "test", "_id" : "3" } }
{ "field1" : "value3" }
{ "update" : {"_id" : "1", "_index" : "test"} }
{ "doc" : {"field2" : "value2"} }
```

数据流允许您跨多个索引存储仅附加的时间序列数据，同时为请求提供单个命名资源。数据流非常适合日志、事件、指标和其他连续生成的数据。

您可以将索引和搜索请求直接提交到数据流。流自动将请求路由到存储流数据的后备索引。您可以使用索引生命周期管理(ILM)自动管理这些支持索引。例如，您可以使用ILM自动将较旧的备份索引移动到较便宜的硬件，并删除不需要的索引。随着数据的增长，ILM可以帮助您降低成本和管理费用。



`备份群集的唯一可靠方法是使用快照和恢复功能。`

## Lucene









## Filebeat





## Logstash

![img](https://img-blog.csdn.net/20180629211932944?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dmczE5OTQ=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

![“logstash workflow”的图片搜索结果](https://gitbook.curiouser.top/assets/logstash-%E7%AE%80%E4%BB%8B%E5%AE%89%E8%A3%85%E9%85%8D%E7%BD%AE-1.png)

![img](https://gitbook.curiouser.top/assets/logstash-%E7%AE%80%E4%BB%8B%E5%AE%89%E8%A3%85%E9%85%8D%E7%BD%AE-2.png)

**logstash数据流历程**

1. 首先有一个输入数据，例如是一个web.log文件，其中每一行都是一条数据。file imput会从文件中取出数据，然后通过json codec将数据转换成logstash event。
2. 这条event会通过queue流入某一条pipline处理线程中，首先会存放在batcher中。当batcher达到处理数据的条件（如一定时间或event一定规模）后，batcher会把数据发送到filter中，filter对event数据进行处理后转到output，output就把数据输出到指定的输出位置。
3. 输出后还会返回ACK给queue，包含已经处理的event，queue会将已处理的event进行标记。

**queue分类**

- In Memory： 在内存中，固定大小，无法处理进程crash. 机器宕机等情况，会导致数据丢失。
- Persistent Queue：可处理进程crash情况，保证数据不丢失。保证数据至少消费一次；充当缓冲区，可代替kafka等消息队列作用。
- Dead Letter Queues：存放logstash因数据类型错误等原因无法处理的Event



**Persistent Queue（PQ）处理流程**

1. 一条数据经由input进入PQ，PQ将数据备份在disk，然后PQ响应input表示已收到数据；
2. 数据从PQ到达filter/output，其处理到事件后返回ACK到PQ；
3. PQ收到ACK后删除磁盘的备份数据；

```shell
queue.type: persisted
path.queue: /usr/share/logstash/data    #队列存储路径；如果队列类型为persisted，则生效
queue.page_capacity: 250mb         #队列为持久化，单个队列大小
queue.max_events: 0               #当启用持久化队列时，队列中未读事件的最大数量，0为不限制
queue.max_bytes: 1024mb           #队列最大容量
queue.checkpoint.acks: 1024       #在启用持久队列时强制执行检查点的最大数量,0为不限制
queue.checkpoint.writes: 1024     #在启用持久队列时强制执行检查点之前的最大数量的写入事件，0为不限制
queue.checkpoint.interval: 1000   #当启用持久队列时，在头页面上强制一个检查点的时间间隔

# pipeline线程数，官方建议是等于CPU内核数
pipeline.workers: 24
# 实际output时的线程数
pipeline.output.workers: 24
# 每次发送的事件数
pipeline.batch.size: 3000
# 发送延时
pipeline.batch.delay: 5

node.name:   #节点名称，便于识别
path.data:   #持久化存储数据的文件夹，默认是logstash home目录下的data
path.config: #设定pipeline配置文件的目录（如果指定文件夹，会默认把文件夹下的所有.conf文件按照字母顺序拼接为一个文件）
path.log:    #设定pipeline日志文件的目录
pipeline.workers: #设定pipeline的线程数（filter+output），优化的常用项
pipeline.batch.size/delay: #设定批量处理数据的数据和延迟
queue.type: #设定队列类型，默认是memory
queue.max_bytes: #队列总容量，默认是1g
```

### 死信队列

![Diagram showing pipeline reading from the dead letter queue](https://www.elastic.co/guide/en/logstash/current/static/images/dead_letter_queue.png)







| Setting                        | Description                                                  | Default value                                                |
| ------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `node.name`                    | A descriptive name for the node.                             | Machine’s hostname                                           |
| `path.data`                    | The directory that Logstash and its plugins use for any persistent needs. | `LOGSTASH_HOME/data`                                         |
| `pipeline.id`                  | The ID of the pipeline.                                      | `main`                                                       |
| `pipeline.java_execution`      | Use the Java execution engine.                               | true                                                         |
| `pipeline.workers`             | The number of workers that will, in parallel, execute the filter and output stages of the pipeline. If you find that events are backing up, or that the CPU is not saturated, consider increasing this number to better utilize machine processing power. | Number of the host’s CPU cores                               |
| `pipeline.batch.size`          | The maximum number of events an individual worker thread will collect from inputs before attempting to execute its filters and outputs. Larger batch sizes are generally more efficient, but come at the cost of increased memory overhead. You may need to increase JVM heap space in the `jvm.options` config file. See [Logstash Configuration Files](https://www.elastic.co/guide/en/logstash/7.4/config-setting-files.html) for more info. | `125`                                                        |
| `pipeline.batch.delay`         | When creating pipeline event batches, how long in milliseconds to wait for each event before dispatching an undersized batch to pipeline workers. | `50`                                                         |
| `pipeline.unsafe_shutdown`     | When set to `true`, forces Logstash to exit during shutdown even if there are still inflight events in memory. By default, Logstash will refuse to quit until all received events have been pushed to the outputs. Enabling this option can lead to data loss during shutdown. | `false`                                                      |
| `pipeline.plugin_classloaders` | (Beta) Load Java plugins in independent classloaders to isolate their dependencies. | `false`                                                      |
| `path.config`                  | The path to the Logstash config for the main pipeline. If you specify a directory or wildcard, config files are read from the directory in alphabetical order. | Platform-specific. See [Logstash Directory Layout](https://www.elastic.co/guide/en/logstash/7.4/dir-layout.html). |
| `config.string`                | A string that contains the pipeline configuration to use for the main pipeline. Use the same syntax as the config file. | None                                                         |
| `config.test_and_exit`         | When set to `true`, checks that the configuration is valid and then exits. Note that grok patterns are not checked for correctness with this setting. Logstash can read multiple config files from a directory. If you combine this setting with `log.level: debug`, Logstash will log the combined config file, annotating each config block with the source file it came from. | `false`                                                      |
| `config.reload.automatic`      | When set to `true`, periodically checks if the configuration has changed and reloads the configuration whenever it is changed. This can also be triggered manually through the SIGHUP signal. | `false`                                                      |
| `config.reload.interval`       | How often in seconds Logstash checks the config files for changes. | `3s`                                                         |
| `config.debug`                 | When set to `true`, shows the fully compiled configuration as a debug log message. You must also set `log.level: debug`. WARNING: The log message will include any *password* options passed to plugin configs as plaintext, and may result in plaintext passwords appearing in your logs! | `false`                                                      |
| `config.support_escapes`       | When set to `true`, quoted strings will process the following escape sequences: `\n` becomes a literal newline (ASCII 10). `\r` becomes a literal carriage return (ASCII 13). `\t` becomes a literal tab (ASCII 9). `\\` becomes a literal backslash `\`. `\"` becomes a literal double quotation mark. `\'` becomes a literal quotation mark. | `false`                                                      |
| `modules`                      | When configured, `modules` must be in the nested YAML structure described above this table. | None                                                         |
| `queue.type`                   | The internal queuing model to use for event buffering. Specify `memory` for legacy in-memory based queuing, or `persisted` for disk-based ACKed queueing ([persistent queues](https://www.elastic.co/guide/en/logstash/7.4/persistent-queues.html)). | `memory`                                                     |
| `path.queue`                   | The directory path where the data files will be stored when persistent queues are enabled (`queue.type: persisted`). | `path.data/queue`                                            |
| `queue.page_capacity`          | The size of the page data files used when persistent queues are enabled (`queue.type: persisted`). The queue data consists of append-only data files separated into pages. | 64mb                                                         |
| `queue.max_events`             | The maximum number of unread events in the queue when persistent queues are enabled (`queue.type: persisted`). | 0 (unlimited)                                                |
| `queue.max_bytes`              | The total capacity of the queue in number of bytes. Make sure the capacity of your disk drive is greater than the value you specify here. If both `queue.max_events` and `queue.max_bytes` are specified, Logstash uses whichever criteria is reached first. | 1024mb (1g)                                                  |
| `queue.checkpoint.acks`        | The maximum number of ACKed events before forcing a checkpoint when persistent queues are enabled (`queue.type: persisted`). Specify `queue.checkpoint.acks: 0` to set this value to unlimited. | 1024                                                         |
| `queue.checkpoint.writes`      | The maximum number of written events before forcing a checkpoint when persistent queues are enabled (`queue.type: persisted`). Specify `queue.checkpoint.writes: 0` to set this value to unlimited. | 1024                                                         |
| `queue.checkpoint.retry`       | When enabled, Logstash will retry once per attempted checkpoint write for any checkpoint writes that fail. Any subsequent errors are not retried. This is a workaround for failed checkpoint writes that have been seen only on filesystems with non-standard behavior such as SANs and is not recommended except in those specific circumstances. | `false`                                                      |
| `queue.drain`                  | When enabled, Logstash waits until the persistent queue is drained before shutting down. | `false`                                                      |
| `dead_letter_queue.enable`     | Flag to instruct Logstash to enable the DLQ feature supported by plugins. | `false`                                                      |
| `dead_letter_queue.max_bytes`  | The maximum size of each dead letter queue. Entries will be dropped if they would increase the size of the dead letter queue beyond this setting. | `1024mb`                                                     |
| `path.dead_letter_queue`       | The directory path where the data files will be stored for the dead-letter queue. | `path.data/dead_letter_queue`                                |
| `http.host`                    | The bind address for the metrics REST endpoint.              | `"127.0.0.1"`                                                |
| `http.port`                    | The bind port for the metrics REST endpoint.                 | `9600`                                                       |
| `log.level`                    | 设置Logstash日志输出级别 可用值：`fatal error warn info debug trace` | `info`                                                       |
| `log.format`                   | The log format. Set to `json` to log in JSON format, or `plain` to use `Object#.inspect`. | `plain`                                                      |
| `path.logs`                    | The directory where Logstash will write its log to.          | `LOGSTASH_HOME/logs`                                         |
| `path.plugins`                 | Where to find custom plugins. You can specify this setting multiple times to include multiple paths. Plugins are expected to be in a specific directory hierarchy: `PATH/logstash/TYPE/NAME.rb` where `TYPE` is `inputs`, `filters`, `outputs`, or `codecs`, and `NAME` is the name of the plugin. | Platform-specific. See [Logstash Directory Layout](https://www.elastic.co/guide/en/logstash/7.4/dir-layout.html) |

## Kibana