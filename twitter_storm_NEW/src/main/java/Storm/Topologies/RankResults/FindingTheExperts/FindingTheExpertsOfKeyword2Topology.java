package Storm.Topologies.RankResults.FindingTheExperts;

import Storm.Bolts.FileWriterBolt;
import Storm.Bolts.Rank.FindTheExpertsBolt;
import Storm.Spouts.ProcessMergedFeaturesOfAKeywordSpout;
import Storm.Spouts.Rank.ProcessMapsWithGaussianProbabilitiesWithNanValuesSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

/**
 * Created by christina on 29/8/2016.
 */
public class FindingTheExpertsOfKeyword2Topology {
    public static final String PATH_TO_EXPERTS_FILE="/home/christina/twitter_storm_NEW/Gaussian_Probabilities/gaussian_probabilities_of_clustered_features_of_keyword2.txt";

    public static final String PATH_TO_MERGED_FEATURES_OF_KEYWORD="/home/christina/twitter_storm_NEW/Merged_Features_Of_Keywords/merged_features_of_keyword2.txt";
    public static final String PATH_TO_EXPERTS_OF_KEYWORD="/home/christina/twitter_storm_NEW/ExpertsOfKeywords/ExpertsOfKeyword2.txt";


    public static void main(String[] args) throws AlreadyAliveException,Exception{
        TopologyBuilder topologyBuilder=new TopologyBuilder();

        topologyBuilder.setSpout("PROCESS_GAUSSIAN_PROBABILITIES_FROM_TEXT_FILE",new ProcessMapsWithGaussianProbabilitiesWithNanValuesSpout(PATH_TO_EXPERTS_FILE));

        topologyBuilder.setSpout("PROCESS_AUTHORS_AND_MERGED_FEATURES_OF_KEYWORD",new ProcessMergedFeaturesOfAKeywordSpout(PATH_TO_MERGED_FEATURES_OF_KEYWORD));

        topologyBuilder.setBolt("FIND_EXPERTS_OF_KEYWORD_AND_THEIR_FEATURES",new FindTheExpertsBolt()).shuffleGrouping("PROCESS_AUTHORS_AND_MERGED_FEATURES_OF_KEYWORD");
        topologyBuilder.setBolt("WRITE_EXPERTS_OF_KEYWORD_AND_FEATURES_TO_TEXT_FILE",new FileWriterBolt(PATH_TO_EXPERTS_OF_KEYWORD)).shuffleGrouping("FIND_EXPERTS_OF_KEYWORD_AND_THEIR_FEATURES");


        Config config = new Config();
        if (args != null && args.length > 0) {
            config.setNumWorkers(5);
            config.setNumAckers(10);
            config.setMaxSpoutPending(100);
            StormSubmitter.submitTopology("Finding_The_Experts_Of_Keyword2", config, topologyBuilder.createTopology());
        } else {
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology("Finding_The_Experts_Of_Keyword2", config, topologyBuilder.createTopology());
            Utils.sleep(2*60*1000);
            localCluster.killTopology("Finding_The_Experts_Of_Keyword2");
            localCluster.shutdown();
        }
    }

}
