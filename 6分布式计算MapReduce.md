# 6-分布式计算MapReduce
## MapReduce
* MapReduce分布式计算框架最初是由Google公司于2004年提出的。它不仅仅是一种分布式计算模型，同时也是一整套构建在大规模普通商业PC 之上的批处理计算框架
* 该计算框架可以处理以PB计的数据，并提供了简易应用接口，将系统容错及任务调度等设计分布式计算系统时需考虑的复杂实现很好地封装在内，使得应用开发者只需关注应用逻辑

## 批处理计算
* 批处理是大数据计算中一类常见的计算任务，主要操作大容量静态数据集，并在计算过程完成后返回结果
* 批处理模式中使用的数据集通常具有下列特征：
  * 有界——批处理数据集代表数据的有限集合；
  * 持久——数据通常始终存储在某种类型的持久存储位置中
  * 大量——批处理操作通常是处理极为海量数据集的唯一方法
* 批处理需要访问全套记录才能完成的计算工作。例如计算总数，必须将数据集作为一个整体加以处理。处理需要付出大量时间，不适合对处理时间要求较高的场合。

## MapReduce计算模型
<img width="613" alt="12b25d47ef9bca1f51336bb3d0825f2" src="https://github.com/user-attachments/assets/d7ce93cd-5c99-4ae8-ab2e-bdc15a814557">


## MapReduce执行过程
<img width="625" alt="53656b08a257b80024f0e6130e8548f" src="https://github.com/user-attachments/assets/59f14d17-b565-4a04-aea1-8f46edafdcb4">


## MapReduce计算模式
* 求和模式(Summarization Pattern)
* 过滤模式(Filtering Pattern)
* 数据组织模式(Data Organization Pattern)

## MapReduce数值求和
<img width="647" alt="43232b7d40ab4a2363e6d86a8a1cdd7" src="https://github.com/user-attachments/assets/f2ce48be-ecf0-4730-9df9-50d69c777396">


<img width="623" alt="f15b4a38118993d714a4cbcc689eb62" src="https://github.com/user-attachments/assets/e2dd4a73-914d-46c2-bbd1-ff55e182e48d">

<img width="653" alt="8bfaa5f55cff7e8803795d86be5848d" src="https://github.com/user-attachments/assets/d282b3bf-736d-4026-ab91-3e23a1552465">


## MapReduce中的重要概念
* Combiner可以看做局部的Reducer，它的作用是合并相同的key对应的value。优势是能够减少MapTask输出的数据量（即磁盘IO）。但是并不是所有的场景都可以使用Combiner
* Partitioner决定了Map Task输出的每条数据交给哪个Reduce Task来处理。Partitioner有两个功能：
  * （1）均衡负载。它尽量将工作均匀地分配给不同的Reduce。
  * （2）效率。它的分配速度一定要非常快。Partitioner的默认实现：hash(key)mod R，这里的R代表Reduce Task的数目，意思就是对key进行hash处理然后取模
  
* 从Map输出到Reduce输入的整个过程可以广义地称为Shuffle。Shuffle横跨Map端和Reduce端，在Map端包括Spill过程，在Reduce端包括copy和sort过程，copy是从相应的Map节点中拉取需要的结果到Reduce中计算，一般Reduce是一边copy一边sort。


## 求和模式(Summarization Pattern)
* Mapper以需要统计的数值类型对象ID作为Key，其对应的数值作为Value，如果使用Combiner 会极大地减少Shuffle阶段的网络传输量。另外，Partitioner的设计也很重要，决定交由哪个Reducer来处理，但可能会导致数据分布倾斜(Skewed)。通过Shuffle阶段，MapReduce 将相同对象传递给同一个Reducer，Reducer则对相同对象的若干Value进行数学统计计算， 得到最终结果。

<img width="641" alt="41ec55f6595885e0f8e3dc4b6479c82" src="https://github.com/user-attachments/assets/27650d34-9e8d-4f49-9805-0ebc16773e76">


## 过滤模式(Filtering Pattern)
<img width="647" alt="cd676e1fd89c222c87be6517439339d" src="https://github.com/user-attachments/assets/8f325829-a1fb-4ea9-a288-bd47baeebbc1">


<img width="647" alt="d7251d1aeae0eb2d0d4c80e45eab785" src="https://github.com/user-attachments/assets/197d33a0-059b-4180-a889-a83859dd7dc2">


## 数据组织模式(Data Organization Pattern)
* 数据分片：需要对数据记录进行分类，比如可以将所有记录按照日期进行分类，将同一天的数据放到一起以做后续数据分析。

<img width="338" alt="f84ed1aab6bb7de9b28647f81205f92" src="https://github.com/user-attachments/assets/52945e2f-4017-48a9-8700-3d5827e1b6f7">


* Mapper和Reducer非常简单，其重点在Partitioner策略的设计。通过改变Partition策略，将相同标准的数据经过Shuffle过程放到一起。由同一个Reducer来输出，即可达到按需数据分片的目的


* 全局排序：对海量数据进行全局排序，例如：针对10亿个网页进行单词统计之后，根据出现次数由高到底对所有单词排序
* Mapper 逻辑很简单，只需要将记录中要排序的字段作为Key，记录内容作为Value输出即可。如果设定一个Reducer，那么Reduce过程不需要做额外工作，只需以原样输出即可，因为Reduce过程已经对所有数据进行了全局排序。但如果设定多个Reducer，那么需要在Partition策略上做些研究，因为尽管每个Reducer负责的部分数据是有序的，但是多个Reducer产生了多份部分有序结果，仍然没有得到所需要的全局排序结果。如何解决？
  * 可以通过Partition策略，将数据分发到不同Reducer的时候，保证不同Reducer处理一个范围区间的记录。

## MapReduce实例——文本分析
* 文本中单词统计：给定一个巨大的文本（如1TB），如何计算单词出现的数目？

<img width="638" alt="1a76d5b6e002057770c2040ea7afe69" src="https://github.com/user-attachments/assets/466ff29c-f547-4dac-8a08-317713c990e1">

<img width="619" alt="a23808315e77cff5b703422bc357168" src="https://github.com/user-attachments/assets/2f97ceed-d1ec-42ab-8ae4-0c2c9b9b35d4">

<img width="566" alt="2282e84e282d0cd943edafe7f90c64a" src="https://github.com/user-attachments/assets/78fc3267-3bd8-4948-b73d-c4db6202939f">

<img width="540" alt="f6887f098fec45be1db7d6aebfcfd3e" src="https://github.com/user-attachments/assets/94a9ba58-558f-4ce8-bc2c-539368f6b4fe">


## MapReduce实例——页面点击统计
* 页面点击统计：对于互联网网站来说，通常是通过Log形式将用户行为记载下来。最常见的Log挖掘项目之一是统计页面点击数，即网站各个页面在一定时间段内各自有多少访问量。

<img width="634" alt="206b0e74ee7305758595539f6a9758c" src="https://github.com/user-attachments/assets/0efe7dea-50d2-48ef-99d1-0404ce3ca976">


## MapReduce实例——专利引用统计
* 专利引用统计：一个真实的数据集cite75_99.txt（专利引用数据）,它来自美国国家经济研究局提供的美国专利数据，网址为http://www.nber.org/patents/。部分数据如下：

<img width="651" alt="c8611ae9aedf3b35cd676c201c4b78e" src="https://github.com/user-attachments/assets/e365e766-13b7-4a43-9487-4fd667ef3e18">


## MapReduce计算特点
* 具有极强的可扩展性，可以在数干台机器上并发执行
* 具有很好的容错性，即使集群机器发生故障，一般情况下也不会影响任务的正常执行
* 具有简单性， 用户只需完成Map和Reduce 函数即可完成大规模数据的并行处理


## MapReduce计算缺点
* MapReduce运算机制的优势是数据的高吞吐量、支持海量数据处理的大规模并行处理、细粒度的容错，但是并不适合对时效性要求较高的应用场景，比如交互式查询或者流式计算。也不适合迭代运算类的机器学习及数据挖掘类应用。原因如下：
  * 第一、Map和Reduce任务启动时间较长。因为对于批处理任务来说，其任务启动时间相对后续任务执行时间来说所占比例并不大，所以可忽略不计。但是对于时效性要求高的应用，其启动时间与任务处理时间相比太高。
  * 第二、在一次应用任务执行过程中， MapReduce计算模型存在多处的磁盘读/写及网络传输过程。比如初始的数据块读取、Map任务的中间结果输出、Shuffle阶段网络传输、Reduce阶段的磁盘读及GFS写入等。对于迭代类机器学习应用来说，往往需要一个MapReduce任务反复迭代进行，此时磁盘读/写及网络传输需要反复进行多次，导致任务效率低下。

## 典型迭代算法——K-Means（聚类）
* 聚类分析：根据“物以类聚”原理，将本身尚未归类的样本根据多个维度（多个属性）聚集成不同的组，这样的一组数据对象的集合叫做簇或群组。
* 聚类目标——经过划分后，使得
  * 属于同一群组的样本之间彼此足够相似
  * 属于不同群组的样本应该足够不相似

* 聚类与分类的区别：聚类没有事先预定的类别，不需要人工标注和预先训练分类器，类别在聚类过程中自动生成 。聚类被视为一种无监督的学习
* K均值算法，概括起来有五个步骤：
  * 设定一个数K，表明总共有几个群簇（组）；
  * 从所有实例中随机选择K个实例，分别代表一个群簇的初始中心
  * 对剩余的每个实例，根据其与各个组的初始中心的距离，将它们分配到离自己最近的一个群簇中
  * 然后，更新群簇中心，即：重新计算得出每个群簇的新的中心点
  * 这个过程不断重复（即：重复第3、4步），直到每个群簇中心不再变化，即直到所有实例在K组分布中都找到离自己最近的群簇。或者达到最大迭代次数


## K-Means算法MapReduce化
<img width="648" alt="d751575f7415d5d64e6e1980a71cb94" src="https://github.com/user-attachments/assets/5dca91b9-75ad-4154-9347-1840a8b39e5d">

* 令 k = 2，欲生成 cluster-0 和 cluster-1
* 随机选取A(1,1)作为cluster-0的中心，C(4,3)作为cluster-1的中心
* 假定将所有数据分布到2个节点 node-0 和 node-1 上,即node-0 ：A(1,1)和C(4,3)，node-1 ：B(2,1)和D(5,4)
* 首先创建一个存储聚类中心的全局文件

<img width="628" alt="90fd2768b0a28e88152faa4c9a85228" src="https://github.com/user-attachments/assets/cff343c2-0f01-46a8-ae7b-75373b4128da">


* Map阶段：
  * 每个节点读取全局文件，以获得上一轮迭代生成的cluster centers等信息
  * 计算本节点上的每一个点到各个cluster center的距离
  * 对于每一个数据点，可以得到<cluster assigned to , 数据点自身>

<img width="643" alt="a9390e1d6f511e3852c67b34fb9291e" src="https://github.com/user-attachments/assets/21016a19-aa98-46a2-a901-aefe56476154">


* Combine阶段：
  * 利用combiner减少map阶段产生的大量数据
  * Combiner计算统一簇中所有数据点的均值，以及这些数据点的个数
  * 然后，为每一个cluster发送 key-value pair
    * key - cluster id,
    * value -【 # of data points of this cluster， mean】

<img width="659" alt="6738a91dec9ac44221eded3f3d912f9" src="https://github.com/user-attachments/assets/d9562567-39be-4924-9d48-0483951388e9">


* Reduce阶段：
  * 由于map阶段产生的key是cluster-id，所以每个 cluster的全部数据将被发送同一个reducer，包括：
    * 该cluster 的id
    * 该cluster的数据点的均值，及对应于该均值的数据点的个数
  * 然后经计算后输出
    * 当前的迭代计数
    * cluster id
    * cluster center
    * 属于该cluster center的数据点的个数



<img width="654" alt="fc213c347d6faece9635d88e015d7ec" src="https://github.com/user-attachments/assets/47b76b8c-1be9-412f-a99b-ff803c16bdd8">

<img width="655" alt="274a1cb7d3d28f956c37c4a9b9d510a" src="https://github.com/user-attachments/assets/3e25d30f-7a6c-4ebf-8035-47c299bbbde2">

<img width="632" alt="3114de411ad2f7a3e00c4dbd02c5323" src="https://github.com/user-attachments/assets/8c140878-1001-451d-93b2-f368da99c469">

