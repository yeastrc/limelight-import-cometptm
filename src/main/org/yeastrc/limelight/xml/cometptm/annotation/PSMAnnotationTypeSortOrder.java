package org.yeastrc.limelight.xml.cometptm.annotation;

import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.limelight.xml.cometptm.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class PSMAnnotationTypeSortOrder {

	public static List<SearchAnnotation> getPSMAnnotationTypeSortOrder( boolean haveIProphetData ) {
		List<SearchAnnotation> annotations = new ArrayList<SearchAnnotation>();

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName(PSMAnnotationTypes.COMET_ANNOTATION_TYPE_FDR);
			annotation.setSearchProgram(Constants.PROGRAM_NAME_COMET_PTM);
			annotations.add(annotation);
		}

		{
			SearchAnnotation annotation = new SearchAnnotation();
			annotation.setAnnotationName(PSMAnnotationTypes.COMET_ANNOTATION_TYPE_EVALUE);
			annotation.setSearchProgram(Constants.PROGRAM_NAME_COMET_PTM);
			annotations.add(annotation);
		}



		return annotations;

	}

}
