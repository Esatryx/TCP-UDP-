# 7-Hadoop&spark
## Hadoop简介
* 开源Apache项目，灵感来源于Google的论文
* Hadoop核心组件包括：
  * 分布式文件系统（HDFS）
  * 分布式计算构架（MapReduce）
* 使用Java编写
* 运行平台：Linux

## HDFS：Hadoop分布式文件系统
* HDFS是Hadoop中的大规模分布式文件系统，模仿GFS开发的开源系统。HDFS适合存储大文件并为之提供高吞吐量的顺序读/写访问，不太适合大量随机读的应用场景，也不适合存储大量小文件等应用场景。
* Hadoop 1.x版本中HDFS的整体架构由四部分构成：NameNode 、DataNode 、Secondary NameNode以及客户端。NameNode类似于GFS的主控服务器, DataNode类似于GFS 的Chunk服务器

## HDFS整体架构
<img width="722" alt="6115f327977dac55815df8c6d17b4fe" src="https://github.com/user-attachments/assets/47cff9e6-fe5b-48a5-b9ec-8941865ad0d5">

## HDFS：NameNode 
* NameNode管理整个分布式文件系统的元数据，包括文件目录树结构、文件到数据块Block的映射关系、Block副本及其存储位置等各种管理数据
* 管理数据保存在内存的同时，在磁盘保存两个元数据管理文件：fsimage和editlog。
* NameNode还负责DataNode的状态监控，通过短时间间隔的心跳来传递管理信息和数据信息。通过这种方式的信息传递，NameNode可以获知每个DataNode保存的Block信息、DataNode的健康状况、命令DataNode启动停止等

## HDFS：Secondary NameNode
* Secondary NameNode的职责并非是NameNode的热备机，而是定期从NameNode拉取fsimage和editlog 文件，并对这两个文件进行合并，形成新的fsimage文件并传给NameNode。目的是为了减轻NameNode的工作压力。NameNode本身不做这种合并操作，所以本质上Secondary NameNode是个提供检查点功能服务的服务器

## HDFS：DataNode和客户端
* DataNode负责数据块的实际存储和读写工作，HDFS语境下一般将数据块称为Block而非Chunk。Block默认大小为64MB。客户端上传大文件时，HDFS会自动将其切割成固定大小的Block。为了保证数据可用性，每个Block会以多备份的形式存储，默认备份个数为3
* 客户端和NameNode联系获取所需读写文件的元数据，实际的读写都是和DataNode直接通信完成。其读写流程与GFS读写流程基本一致，不同点在于不支持客户端对同一文件的并发写操作， 同一时刻只能有一个客户端在文件末尾进行追加写操作。

## Hadoop MapReduce示例
<img width="779" alt="c7fb8e4a53bbbd5a4e969bcb15253c2" src="https://github.com/user-attachments/assets/6e596908-d8ad-4c56-af9a-c2e40adeb37a">

## Hadoop 1.0 v.s Hadoop 2.0
<img width="779" alt="3352cd13e9ed1d781a80c79c497d8b0" src="https://github.com/user-attachments/assets/298ee2aa-dd70-498b-b87a-2e15470d0125">

## YARN——资源管理与调度系统
* 资源管理与调度的核心目标：使得整个集群的大量资源在能够实现更高资源利用率的同时加快所有计算任务的整体完成速度
* 当前发展趋势：在集群硬件层之上抽象出一个功能独立的资源管理系统，将所有可用资源当作一个整体来进行管理，并对其他所有计算任务提供统一的资源管理与调度框架和接口
* YARN基本架构

<img width="767" alt="489b72858a126743816f9eaac9b22d7" src="https://github.com/user-attachments/assets/9e6314cb-7f20-4250-8121-8c584638fe1e">

* YARN是个典型的两级调度器。
  * ResourceManager(RM)负责整个集群的资源管理功能，类似于中央调度器
  * 每个任务单独有一个ApplicationMaster(AM)负责完成任务所需资源的申请管理与任务生命周期管理功能，类似于二级调度器，AM负责向RM 申请作业所需资源，并在作业的众多任务中进行资源分配与协调。

* 资源管理器RM部分：
  * 主要负责全局的资源管理工作，其内部主要功能部件包括：调度器、AMService、Client-RM接口以及RM-NM接口。
  * 调度器主要提供各种调度策略，支持可插拔方式，系统管理者可以制定全局的资源分配策略。
  * AMService负责系统内所有AM的启动与运行状态管理。
  * Client-RM接口负责按照一定协议管理客户提交的作业。
  * RM-NM接口主要和各个机器的NM通过心跳方式进行通信，以此来获知各个机器可用的资源以及机器是否产生故障等信息。
 
* 应用服务器AM部分：
  * AM的功能类似于Hadoop 1.0的JobTracker，负责向RM申请启动任务所需的资源，同时协调作业内各个任务的运行过程。
  * AM像普通任务一样运行在某台机器的容器内。RM的AMS负责为作业的AM申请资源并启动它，使得整个作业能够运转起来。之后各种任务管理工作都交由AM 来负责
  * AM作为二级调度器，也负责任务间资源分配时的数据局部性等优化调度策略。

* 节点管理器NM部分：
  * NM是部署在每台机器上的，主要负责机器内的容器资源管理，比如容器间的依赖关系、监控容器执行以及为容器提供资源隔离等各种服务等
  * NM启动以后，向RM进行注册，之后通过心跳方式向RM汇报节点状态并执行RM发送来的命令
  * NM也接收AM发来的命令，比如启动或者杀死某个容器内运行的任务等。


## 资源管理与调度
* 独立资源管理与调度系统的优势：
  * 集群整体资源利用率高，可根据不同计算任务的即时需要动态分配资源
  * 可增加数据共享能力，所有资源对所有任务可用， 只需存储一份即可
  * 支持多类型计算框架和多版本计算框架，不仅可以运行批处理、流式计算、图计算等多种类型特点各异的计算系统，也可以支持多版本计算框架

* 概念模型：主要强调三要素，资源组织模型，调度策略和任务组织模型

<img width="774" alt="2f52e07db9d30edd75cc83192e07d17" src="https://github.com/user-attachments/assets/1772cdc3-dc34-44ff-b109-5471153d0f34">


## Spark简介
* Spark项目于2009年诞生于加州大学伯克利分校AMPLab。是专为大规模数据处理而设计的快速通用的计算框架。在保留了MapReduce优点基础上，实现了基于内存的计算，使得计算时效性有了很大的提高。

## Spark V.S Hadoop
* 处理问题的层面不同：Hadoop被看做是分布式数据基础设施，包括了存储（HDFS）和计算（MapReduce）。Spark是专门用来对大数据进行处理的工具，目标是具备通用性和高效性。自身没有数据存储功能。可处理来自HDFS或其他分布式文件系统的数据。
* Spark数据处理速度远超Hadoop：Hadoop是磁盘级计算，计算时需要在磁盘中读取数据。Spark在内存中以接近“实时”的时间完成所有的数据分析，与Hadoop相比快近100倍。

## 关于Spark内存计算的理解 
* 内存计算不是Spark的特性：Spark只是利用内存来实现数据的缓存，而不是将数据持久化在内存中。Spark允许我们使用内存缓存以及LRU替换规则。
* Spark比Hadoop快的原因：
  * task启动时间比较快，Spark是fork出线程；而MR是启动一个新的进程
  * Spark只有在shuffle的时候才会将数据放在磁盘，而MR却不是
  * 典型的MR工作流是由很多MR作业组成的，他们之间的数据交互需要把数据持久化到磁盘才可以；而Spark支持DAG以及pipelining，在没有遇到shuffle完全可以不把数据缓存到磁盘。
  * 虽然目前HDFS也支持缓存，但是一般来说，Spark的缓存功能更加高效，特别是在SparkSQL中，我们可以将数据以列式的形式储存在内存中

## Spark特性
<img width="770" alt="a42fa4942065a1dc57fe697e29b5619" src="https://github.com/user-attachments/assets/f30ad7ff-bac1-4883-910a-a08580037ebc">


## Spark生态系统
<img width="785" alt="553c2c6799b457b4ac9843e74570753" src="https://github.com/user-attachments/assets/1e65caba-abec-4b6c-8b6a-beb524aa2212">

## Spark计算核心层
<img width="770" alt="d77cd7d323a209080870484587b171e" src="https://github.com/user-attachments/assets/a47d9db9-f7b6-48c1-8db8-69aa0aaa7e0b">

## Spark程序基本流程
<img width="739" alt="21c5ebe03a55c86cb876e51743cb2e3" src="https://github.com/user-attachments/assets/d79598b8-f605-47fb-87e0-e7e7d423407e">

## 什么是DAG
* DAG（Directed Acyclic Graph）有向无环图的简称。DAG计算模型往往是指将计算任务在内部分解成为若干个子任务。这些子任务之间由逻辑关系或运行先后顺序等因素被构建成有向无环图结构。
* 大数据处理领域的DAG计算系统一般包含三层架构：
  * 应用表达层（最上层）：通过一定手段将计算任务分解成由若干个子任务形成的DAG结构。这层的核心是表达的便捷性，主要目的是方便应用开发者快速描述或者构建应用
  * DAG执行引擎层（中间层）：主要目的是将上层以特殊方式表达的DAG计算任务通过转换和映射。将其部署到下层的物理机集群中来真正运行。是DAG计算系统的核心部件。
  * 物理机集群（最下层）：由大量物理机搭建的分布式计算环境，计算任务最终执行的场所。

## 什么是RDD
* RDD：弹性分布式数据集（resilient distributed dataset）是Spark的核心数据模型，RDD是Spark中待处理的数据的抽象，它是逻辑中的实体
* 在对RDD进行处理的时候不需要考虑底层的分布式集群，就像在单机上一样即可。
* RDD是只读的分区记录集合。只能基于稳定物理存储中的数据集或在已有的RDD上执行确定性操作来创建
* RDD可以被认为是提供了一种高度限制的共享内存（只读，只能从稳定的存储或已有的RDD创建而来。）

* RDD示例：在系统日志文件中找出HDFS出错的时间

<img width="654" alt="45cecd32caa8e681a4faabf7db93734" src="https://github.com/user-attachments/assets/6edd6a35-671b-4ecb-9d9b-c85c56ca1a96">

## RDD特点
* RDD表示已被分区，不可变的并能够被并行操作的数据集合。可以从程序数据集，HDFS数据，流数据等不同数据集格式来构建相应的RDD
* RDD是可序列化的。RDD也可以cache到内存中
* 在创建RDD时Spark会维护RDD衍生及转换的信息。需要时候，可以从物理存储的数据计算出最终的 RDD
* RDD有两种计算方式：转换（Transformation，返回值还是一个RDD）与操作（Action，返回值不是RDD）

<img width="782" alt="20b9ba8787659d67311f84ff81799cd" src="https://github.com/user-attachments/assets/5382da9f-005e-47a7-a58e-e027aca27008">

## RDD转换与操作与操作函数
<img width="771" alt="38706e4c214c35f07c663cde2c85397" src="https://github.com/user-attachments/assets/56cf80b7-71ec-465d-beb4-ef8764284591">

<img width="753" alt="4214f9584743fd6819607369db23094" src="https://github.com/user-attachments/assets/2d0a8052-7c6e-40c0-ae41-7ecd8e0f63e7">

<img width="772" alt="01f537467c1fdea462e82c9b3b79534" src="https://github.com/user-attachments/assets/9cf0f95b-ff3a-41b9-a6c7-8e92dd45d1b7">

## RDD“血统” 
* 一个作业从开始到结束的计算过程中产生了多个 RDD，RDD之间是彼此相互依赖的，我们把这种父子依赖的关系称之为“血统”。RDD的变换序列均由“血统” 存储下来，为容错提供便利
* RDD的容错机制又称“血统”容错。 要实现这种容错机制，最大的难题就是如何表达父 RDD 和子 RDD 之间的依赖关系
* RDD中的依赖划分成了两种类型：窄依赖 (Narrow Dependencies) 和宽依赖 (Wide Dependencies)

## RDD依赖
* 窄依赖 (Narrow Dependencies) ：是指父RDD的每个分区都只被子RDD的一个分区所使用
* 宽依赖(Wide Dependencies)：就是指父RDD的分区被多个子 RDD 的分区所依赖。

<img width="756" alt="08923ebb217f8cf928bb87833118e8c" src="https://github.com/user-attachments/assets/055584f6-713a-47fc-88bd-ace27b46f299">

## Spark RDD运行原理
* RDD在Spark架构中的运行主要分为三步：
  * 创建 RDD 对象
  * DAGScheduler模块介入运算，计算RDD之间的依赖关系。RDD之间的依赖关系就形成了DAG
  * 每一个JOB被分为多个Stage，划分Stage的一个主要依据是RDD之间的依赖关系，从后往前推，遇到宽依赖就断开，划分为一个stage

<img width="743" alt="32422054b56ec6020922c7a2dec3cd2" src="https://github.com/user-attachments/assets/2e71937e-1d0c-4b5f-8be7-2980dae7e9d0">

<img width="781" alt="4dcad107a663ca8bb525738ef91946f" src="https://github.com/user-attachments/assets/8ed4ca40-21e2-4a26-8671-cbb9b774377f">

## Spark技术堆栈/Spark MLlib架构解析/Spark的高可用性
* Spark使用Spark RDD、 Spark SQL、 Spark Streaming、 MLlib、 GraphX成功解决了大数据领域中，离线批处理、 交互式查询、 实时流计算、 机器学习与图计算等最重要问题
* Spark MLlib 是Spark中可以扩展的机器学习库，它有一系列的机器学习算法和实用程序组成。包括分类、回归、聚类、协同过滤等，还包含一些底层优化方法
* Spark Mlib架构解析

<img width="777" alt="5790cbd73c77c56552d960075124f7f" src="https://github.com/user-attachments/assets/3d388606-5c8d-448a-b6d3-e105ff621545">

* Spark的高可用性

<img width="732" alt="363824d568e8cbfdc73485e24be2cba" src="https://github.com/user-attachments/assets/013dcdea-f065-48bc-98c3-68edc8c6c1f6">
