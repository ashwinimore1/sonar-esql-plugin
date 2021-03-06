/*
 * Sonar ESQL Plugin
 * Copyright (C) 2013-2017 Thomas Pohl and EXXETA AG
 * http://www.exxeta.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exxeta.iss.sonar.esql.check;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.sonar.api.rules.AnnotationRuleParser;
import org.sonar.api.rules.Rule;

import com.google.common.collect.Lists;

public class CheckListTest {

	/**
	 * Enforces that each check declared in list.
	 */
	@Test
	public void count() {
		int count = 0;
		List<File> files = (List<File>) FileUtils.listFiles(new File("src/main/java/com/exxeta/iss/sonar/esql/check/"),
				new String[] { "java" }, false);
		for (File file : files) {
			 String name = file.getName();
		      if (name.endsWith("Check.java") && !name.startsWith("Abstract")) {
				count++;
			}
		}
		assertThat(CheckList.getChecks().size()).isEqualTo(count);
	}

	/**
	 * Enforces that each check has test, name and description.
	 */
	@Test
	public void test() {

		
		
		List<Class> checks = CheckList.getChecks();

	    for (Class cls : checks) {
	      if (!cls.getSimpleName().equals("ParsingErrorCheck")) {
	        String testName = '/' + cls.getName().replace('.', '/') + "Test.class";
	        assertThat(getClass().getResource(testName))
	          .overridingErrorMessage("No test for " + cls.getSimpleName())
	          .isNotNull();
	      }
	    }

	    List<String> keys = Lists.newArrayList();
	    List<Rule> rules = new AnnotationRuleParser().parse("repositoryKey", checks);
	    for (Rule rule : rules) {
	      keys.add(rule.getKey());
	      assertThat(getClass().getResource("/org/sonar/l10n/esql/rules/esql/" + rule.getKey() + ".html"))
	        .overridingErrorMessage("No description for " + rule.getKey())
	        .isNotNull();

	      assertThat(rule.getDescription())
	        .overridingErrorMessage("Description of " + rule.getKey() + " should be in separate file")
	        .isNull();
	    }

	    assertThat(keys).doesNotHaveDuplicates();
	}
}
