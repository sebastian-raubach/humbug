<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright 2017 Sebastian Raubach and Paul Shaw from the
  ~ Information and Computational Sciences Group at JHI Dundee
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

<xsl:stylesheet exclude-result-prefixes="fo" version="2.0" xmlns:fo="http://www.w3.org/1999/XSL/Format"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="barcode-list">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master margin-bottom="{{margin-bottom}}" margin-left="{{margin-left}}" margin-right="{{margin-right}}"
									   margin-top="{{margin-top}}" master-name="simpleA4" page-height="29.7cm" page-width="21cm">
					<fo:region-body column-count="2" column-gap="{{spacing}}"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">
					<fo:block font-size="14pt">
						<xsl:apply-templates select="item"/>
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>
	<xsl:template match="item">
		<fo:block-container font-size="14pt" keep-together.within-column="always" space-before="{{spacing}}" text-align="center">
			<xsl:if test="image">
				<fo:block>
					<fo:external-graphic content-height="scale-to-fit" content-width="scale-to-fit" height="{{max-image-height}}" width="100%">
						<xsl:attribute name="src">url('<xsl:value-of select="image"/>')
						</xsl:attribute>
					</fo:external-graphic>
				</fo:block>
			</xsl:if>
			<fo:block>
				<fo:external-graphic content-height="scale-to-fit" content-width="scale-to-fit" height="{{max-image-height}}" width="100%">
					<xsl:attribute name="src">url('<xsl:value-of select="barcode"/>')
					</xsl:attribute>
				</fo:external-graphic>
			</fo:block>
			<fo:block wrap-option="wrap">
				<xsl:value-of select="name"/>
			</fo:block>
		</fo:block-container>
	</xsl:template>
</xsl:stylesheet>