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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dsl.domain.FoldingRange;
import org.springframework.dsl.domain.FoldingRangeKind;
import org.springframework.dsl.service.DslContext;
import org.springframework.dsl.service.Folderer;

import reactor.core.publisher.Flux;

public class DataflowStreamLanguageFolderer extends AbstractDataflowStreamLanguageService implements Folderer {

	@Override
	public Flux<FoldingRange> fold(DslContext context) {
		return parse(context.getDocument())
			.flatMap(item -> Flux.fromIterable(streamFolds(item))
				.concatWithValues(commentFolds(item).toArray(new FoldingRange[0])));
	}

	private List<FoldingRange> streamFolds(StreamItem item) {
		return Arrays.asList(
			FoldingRange.foldingRange()
				.startLine(item.getRange().getStart().getLine())
				.startCharacter(item.getRange().getStart().getCharacter())
				.endLine(item.getRange().getEnd().getLine())
				.endCharacter(item.getRange().getEnd().getCharacter())
				.kind(FoldingRangeKind.region)
				.build());
	}

	private List<FoldingRange> commentFolds(StreamItem item) {
		return item.getCommentRanges().stream()
			.map(comment -> {
				return FoldingRange.foldingRange()
					.startLine(comment.getStart().getLine())
					.startCharacter(comment.getStart().getCharacter())
					.endLine(comment.getEnd().getLine())
					.endCharacter(comment.getEnd().getCharacter())
					.kind(FoldingRangeKind.comment)
					.build();
			})
			.collect(Collectors.toList());
	}
}
