package AR

import org.apache.spark.mllib.fpm.FPGrowth
import org.apache.spark.{SparkConf, SparkContext}

object Main {

  def main(args:Array[String]){

    val conf = new SparkConf().setAppName("FPGrowthTest")
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