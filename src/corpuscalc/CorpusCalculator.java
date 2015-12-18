package corpuscalc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;

@SuppressWarnings("deprecation")
public class CorpusCalculator {
	
	public static void main(String[] args) throws IOException, URISyntaxException{
		
		JobConf conf = new JobConf(WordCount.class);
		conf.setJobName("WordCount");
		
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		
		conf.setMapperClass(WordCount.Map.class);
		conf.setCombinerClass(WordCount.Reduce.class);
		conf.setReducerClass(WordCount.Reduce.class);
		
		conf.setInputFormat(TextInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path("output1"));

		JobClient.runJob(conf);
		
		JobConf conf1 = new JobConf(Calculator.class);
		conf1.setJobName("Calculator");
		conf1.setMapOutputKeyClass(Text.class);
		conf1.setMapOutputValueClass(Text.class);
		conf1.setMapperClass(Calculator.Map.class);
		conf.setReducerClass(Calculator.Reduce.class);


		conf1.setInputFormat(TextInputFormat.class);
		conf1.setOutputFormat(TextOutputFormat.class);



		FileInputFormat.setInputPaths(conf1, new Path("output1/part-00000"));
		FileOutputFormat.setOutputPath(conf1, new Path("op2"));

		JobClient.runJob(conf1);
		
		JobConf conf2 = new JobConf(ThreeSentence.class);
		conf2.setJobName("ThreeSentence");
		conf2.setMapOutputKeyClass(Text.class);
		conf2.setMapOutputValueClass(Text.class);
		
		DistributedCache.addCacheFile(new File("Corpus.txt").toURI(),conf2);
		//DistributedCache.addCacheFile(new URI("s3n://corpuscalc/Corpus/input/Corpus.txt" + "#Corpus.txt"),conf2);

		conf2.setMapperClass(ThreeSentence.Map.class);
		conf2.setReducerClass(ThreeSentence.Reduce.class);


		conf2.setInputFormat(TextInputFormat.class);
		conf2.setOutputFormat(TextOutputFormat.class);

		FileInputFormat.setInputPaths(conf2, new Path("op2/part-00000"));
		FileOutputFormat.setOutputPath(conf2, new Path("op3"));

		JobClient.runJob(conf2);

	}
}