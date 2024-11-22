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
