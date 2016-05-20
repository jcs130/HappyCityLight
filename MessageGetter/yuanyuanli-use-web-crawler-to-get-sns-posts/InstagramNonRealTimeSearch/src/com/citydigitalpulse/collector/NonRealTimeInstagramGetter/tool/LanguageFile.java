package com.citydigitalpulse.collector.NonRealTimeInstagramGetter.tool;

import java.io.IOException;
import java.util.List;

import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;

public class LanguageFile {
	public static LanguageDetector GetLanguageFile() {
		List<LanguageProfile> languageProfiles = null;
		try {
			languageProfiles = new LanguageProfileReader().readAllBuiltIn();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
				.withProfiles(languageProfiles).build();
		return languageDetector;
	}

}
