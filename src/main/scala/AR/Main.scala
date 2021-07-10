package AR

import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.{SparkConf, SparkContext}

object Main {

  def main(args:Array[String]){

    var spark_memory_fraction = "0.7"
    var spark_memory_storage_Fraction = "0.3"
    var spark_shuffle_spill_compress = "true"
    var spark_memory_offHeap_enable = "true"
    var spark_memory_offHeap_size = "5g"

    var spark_executor_cores_AB = "2"
    var spark_cores_max_AB = "42"

    var spark_executor_cores_CD = "8"
    var spark_cores_max_CD = "168"
    var spark_parallelism_CD = "672"

    var spark_executor_instances = "21"
    var spark_driver_cores = "24"
    var spark_driver_memory = "20g"
    var spark_executor_memory_AB = "20g"
    var spark_executor_memory_CD = "20g"

    val conf = new SparkConf().setAppName("FPGrowth")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.memory.fraction", myConf.spark_memory_fraction)
    conf.set("spark.memory.storageFraction", myConf.spark_memory_storage_Fraction)
    conf.set("spark.shuffle.spill.compress", myConf.spark_shuffle_spill_compress)
    conf.set("spark.memory.offHeap.enable", myConf.spark_memory_offHeap_enable)
    conf.set("spark.memory.offHeap.size", myConf.spark_memory_offHeap_size)
    conf.set("spark.executor.memory", myConf.spark_executor_memory_AB)
    conf.set("spark.driver.cores", myConf.spark_driver_cores)
    conf.set("spark.driver.memory", myConf.spark_driver_memory)
    conf.set("spark.executor.instances", myConf.spark_executor_instances)
    conf.set("spark.cores.max", myConf.spark_cores_max_AB)
    conf.set("spark.executor.cores", myConf.spark_executor_cores_AB)
    val sc = new SparkContext(conf)

    val input_path=args(0)
    val output_path=args(1)
    //最小支持度
    val minSupport=0.092
    //最小置信度
    val minConfidence=0.8
    //数据分区
    val numPartitions=336


    //取出数据
    val data_D = sc.textFile(input_path + "/D.dat", numPartitions)
    // val data_U = sc.textFile(input_path + "/U.dat")
    //把数据通过空格分割
    val purchase = data_D.map(x=>x.split(" "))
    // val user = data_U.map(x=>x.split(" "))
    // purchase.cache()
    // user.cache()

    //创建一个FPGrowth的算法实列
    val fpg = new FPGrowth()
    //设置训练时候的最小支持度和数据分区
    fpg.setMinSupport(minSupport)
    fpg.setNumPartitions(numPartitions)

    //把数据带入算法中
    val model = fpg.run(purchase)

    //查看所有的频繁项集，并且列出它出现的次数
    model.freqItemsets.saveAsTextFile(output_path + "/Freq")

    //通过置信度筛选出推荐规则则
    //antecedent表示前项
    //consequent表示后项
    //confidence表示规则的置信度
    model.generateAssociationRules(minConfidence).saveAsTextFile(output_path + "/Rules")
  }
}