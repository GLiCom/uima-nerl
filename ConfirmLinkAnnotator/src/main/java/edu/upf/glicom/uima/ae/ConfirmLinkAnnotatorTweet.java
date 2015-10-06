package edu.upf.glicom.uima.ae;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.dbpedia.spotlight.uima.types.DBpediaResource;
import org.dbpedia.spotlight.uima.types.TopDBpediaResource;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import edu.upf.glicom.uima.ts.VerifiedDBpediaResource;

public class ConfirmLinkAnnotatorTweet extends JCasAnnotator_ImplBase
{

	public void process(JCas jCas) throws  AnalysisEngineProcessException
	{
		for (NamedEntity entity : select(jCas, NamedEntity.class)) {

			for (DBpediaResource topresource : selectCovered(TopDBpediaResource.class, entity)) {

				int start = topresource.getBegin();
				int end = topresource.getEnd();			
				Double confidence = topresource.getFinalScore();
				String spot = topresource.getCoveredText();

				String nertype = entity.getValue();
				int start_ne = entity.getBegin();
				int end_ne = entity.getEnd();
				int size_ne = end_ne - start_ne;
				int sizedb = end - start;
				if ( (!nertype.equals("PERSON") && confidence > 0.9) || (nertype.equals("PERSON") && size_ne <= sizedb && confidence > 0.6) ) {	//settings for News
			//	if (!spot.equals("RT") && (confidence > 0.9 || !nertype.equals("")))	{  //settings for tweets
				    DBpediaResource a = new VerifiedDBpediaResource(jCas, topresource.getBegin(), topresource.getEnd());
					a.setContextualScore(topresource.getContextualScore());
					a.setFinalScore(topresource.getFinalScore());
					a.setLabel(topresource.getLabel());
					a.setPercentageOfSecondRank(topresource.getPercentageOfSecondRank());
					a.setPriorScore(topresource.getPriorScore());
				    a.setSupport(topresource.getSupport());
				    a.setTypes(topresource.getTypes());
				    a.setUri(topresource.getUri());
				    a.addToIndexes();
				}
			}

		}
	}
}
