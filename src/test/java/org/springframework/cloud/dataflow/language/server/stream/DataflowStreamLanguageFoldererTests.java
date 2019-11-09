/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.dataflow.language.server.stream;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.dataflow.language.server.DataflowLanguages;
import org.springframework.cloud.dataflow.language.server.support.DataFlowOperationsService;
import org.springframework.cloud.dataflow.language.server.support.DataflowCacheService;
import org.springframework.dsl.document.Document;
import org.springframework.dsl.document.TextDocument;
import org.springframework.dsl.domain.FoldingRange;
import org.springframework.dsl.domain.FoldingRangeKind;
import org.springframework.dsl.service.DslContext;

public class DataflowStreamLanguageFoldererTests {

	private final DataflowStreamLanguageFolderer folderer = new DataflowStreamLanguageFolderer();

	@BeforeEach
	public void setup() {
		folderer.setDataflowCacheService(new DataflowCacheService());
		folderer.setDataflowOperationsService(new DataFlowOperationsService());
	}

	@Test
	public void testComments() {
		Document document = new TextDocument("fakeuri", DataflowLanguages.LANGUAGE_STREAM, 0,
				AbstractDataflowStreamLanguageServiceTests.DSL_COMMENTS_IN_MULTI);
		List<FoldingRange> folds = folderer.fold(DslContext.builder().document(document).build()).toStream()
				.collect(Collectors.toList());
		assertThat(folds).hasSize(5);
		List<FoldingRangeKind> kinds = folds.stream().map(f -> f.getKind()).collect(Collectors.toList());
		assertThat(kinds).containsExactlyInAnyOrder(FoldingRangeKind.region, FoldingRangeKind.region,
				FoldingRangeKind.comment, FoldingRangeKind.comment, FoldingRangeKind.comment);
	}
}
