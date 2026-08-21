package com.github.muehmar.gradle.openapi.generator.java.model.promotion;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

/**
 * Contains the promoted pojo itself (which may be unchanged) and a collection of newly created
 * pojos during the promotion. The collection of newly created pojos will contain the promoted pojo
 * too in case it is modified cause of the promotion.
 */
@Value
public class PojoPromotionResult {
  JavaObjectPojo promotedPojo;
  PList<JavaObjectPojo> newPojos;

  public static PojoPromotionResult ofUnchangedPojo(JavaObjectPojo pojo) {
    return new PojoPromotionResult(pojo, PList.empty());
  }

  public PList<JavaObjectPojo> getNewPojosWithPromotedPojoExcluded() {
    return newPojos.filter(newPojo -> not(promotedPojo.equals(newPojo)));
  }
}
