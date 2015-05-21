/*
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.metaverse.analyzer.kettle.step.httppost;

import com.pentaho.dictionary.DictionaryConst;
import com.pentaho.metaverse.api.IMetaverseNode;
import com.pentaho.metaverse.api.IMetaverseObjectFactory;
import com.pentaho.metaverse.api.MetaverseComponentDescriptor;
import com.pentaho.metaverse.api.MetaverseException;
import com.pentaho.metaverse.api.StepField;
import com.pentaho.metaverse.api.analyzer.kettle.step.ExternalResourceStepAnalyzer;
import com.pentaho.metaverse.api.model.IExternalResourceInfo;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.steps.httppost.HTTPPOSTMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The HTTPPostStepAnalyzer is responsible for providing nodes and links (i.e. relationships) between itself and other
 * metaverse entities
 */
public class HTTPPostStepAnalyzer extends ExternalResourceStepAnalyzer<HTTPPOSTMeta> {
  private Logger log = LoggerFactory.getLogger( HTTPPostStepAnalyzer.class );


  @Override
  protected Set<StepField> getUsedFields( HTTPPOSTMeta stepMeta ) {
    Set<StepField> usedFields = new HashSet<>();

    // add url field
    if ( stepMeta.isUrlInField() && StringUtils.isNotEmpty( stepMeta.getUrlField() ) ) {
      usedFields.addAll( createStepFields( stepMeta.getUrlField(), getInputs() ) );
    }

    // add parameters as used fields
    String[] parameterFields = stepMeta.getQueryField();
    if ( ArrayUtils.isNotEmpty( parameterFields ) ) {
      for ( String paramField : parameterFields ) {
        usedFields.addAll( createStepFields( paramField, getInputs() ) );
      }
    }

    // add headers as used fields
    String[] headerFields = stepMeta.getArgumentField();
    if ( ArrayUtils.isNotEmpty( headerFields ) ) {
      for ( String headerField : headerFields ) {
        usedFields.addAll( createStepFields( headerField, getInputs() ) );
      }
    }
    return usedFields;
  }
  @Override
  protected Map<String, RowMetaInterface> getInputRowMetaInterfaces( HTTPPOSTMeta meta ) {
    return getInputFields( meta );
  }

  @Override public IMetaverseNode createResourceNode( IExternalResourceInfo resource ) throws MetaverseException {
    MetaverseComponentDescriptor componentDescriptor = new MetaverseComponentDescriptor(
      resource.getName(), getResourceInputNodeType(), descriptor.getNamespace(), descriptor.getContext()
    );
    IMetaverseNode node = createNodeFromDescriptor( componentDescriptor );
    return node;
  }

  @Override public String getResourceInputNodeType() {
    return DictionaryConst.NODE_TYPE_WEBSERVICE;
  }

  @Override public String getResourceOutputNodeType() {
    return null;
  }

  @Override public boolean isOutput() {
    return false;
  }

  @Override public boolean isInput() {
    return true;
  }

  ////// used in unit testing
  protected void setObjectFactory( IMetaverseObjectFactory objectFactory ) {
    this.metaverseObjectFactory = objectFactory;
  }

  @Override
  public Set<Class<? extends BaseStepMeta>> getSupportedSteps() {
    return new HashSet<Class<? extends BaseStepMeta>>() {
      {
        add( HTTPPOSTMeta.class );
      }
    };
  }
}
