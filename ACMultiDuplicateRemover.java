import java.util.*;

/**
 * 支持多个句子的重复检测和删除的类。
 */
public class ACMultiDuplicateRemover {

    /**
     * 测试用例类
     */
    static class TestCase {
        String name;
        List<String> sentences;
        int minSubstringLength;
        List<String> expectedOutput;

        TestCase(String name, List<String> sentences, int minSubstringLength, List<String> expectedOutput) {
            this.name = name;
            this.sentences = sentences;
            this.minSubstringLength = minSubstringLength;
            this.expectedOutput = expectedOutput;
        }
    }

    /**
     * 主方法，运行所有测试用例并对比预期输出与实际输出。
     */
    public static void main(String[] args) {
        List<TestCase> testCases = Arrays.asList(
                new TestCase("测试样例 1：基本重复删除", Arrays.asList("今天的天气真好，阳光明媚。", "今天的天气真好，适合出去散步。", "阳光明媚的日子总是让人心情愉快。"), 4, Arrays.asList("今天的天气真好，阳光明媚。", "适合出去散步。", "的日子总是让人心情愉快。")),
                new TestCase("测试样例 2：部分重复删除", Arrays.asList("学习编程需要耐心和毅力。", "编程是一项有趣的活动，学习编程能提升逻辑思维。", "耐心和毅力是学习任何技能的关键。"), 4, Arrays.asList("学习编程需要耐心和毅力。", "编程是一项有趣的活动，能提升逻辑思维。", "是学习任何技能的关键。")),
                new TestCase("测试样例 3：无重复", Arrays.asList("苹果是一种健康的水果。", "学习新技能可以提高竞争力。", "旅行能开阔视野，增长见识。"), 4, Arrays.asList("苹果是一种健康的水果。", "学习新技能可以提高竞争力。", "旅行能开阔视野，增长见识。")),
                new TestCase("测试样例 4：全部重复", Arrays.asList("重复的句子内容。", "重复的句子内容。", "重复的句子内容。"), 4, Arrays.asList("重复的句子内容。", "", "")),
                new TestCase("测试样例 5：重叠重复", Arrays.asList("这是一个测试测试用例。", "测试用例可以帮助验证算法。", "通过测试用例，可以发现问题。"), 4, Arrays.asList("这是一个测试测试用例。", "可以帮助验证算法。", "通过，可以发现问题。")),
                new TestCase("测试样例 6：不同长度的重复", Arrays.asList("数据结构与算法是计算机科学的核心。", "算法的设计和分析对于解决问题至关重要。", "数据科学依赖于有效的算法。"), 2, Arrays.asList("数据结构与算法是计算机科学的核心。", "的设计和分析对于解决问题至关重要。", "依赖于有效的。")),
                new TestCase("测试样例 7：特殊字符和空格", Arrays.asList("Hello, world! 欢迎来到编程世界。", "欢迎来到编程世界，这是一个挑战。", "编程世界充满了无限可能。"), 4, Arrays.asList("Hello, world! 欢迎来到编程世界。", "，这是一个挑战。", "充满了无限可能。")),
                new TestCase("测试样例 8：多个重复子串", Arrays.asList("春天来了，花儿开始绽放。", "花儿开始绽放，春天的气息扑面而来。", "春天的气息让人感到舒适，花儿绽放美丽。"), 4, Arrays.asList("春天来了，花儿开始绽放。", "，春天的气息扑面而来。", "让人感到舒适，花儿绽放美丽。")),
                new TestCase("测试样例 9：重复子串位于句首和句尾", Arrays.asList("成功需要努力和坚持。", "努力和坚持是成功的关键。", "只有坚持才能取得成功。"), 4, Arrays.asList("成功需要努力和坚持。", "是成功的关键。", "只有坚持才能取得成功。")),
                new TestCase("测试样例 10：部分重叠的重复子串", Arrays.asList("技术的进步推动了社会的发展。", "社会的发展离不开技术的支持。", "技术支持是社会进步的重要因素。"), 4, Arrays.asList("技术的进步推动了社会的发展。", "离不开技术的支持。", "技术支持是社会进步的重要因素。")),
                new TestCase("测试样例 11：空句子列表", Collections.emptyList(), 4, Collections.emptyList()),
                new TestCase("测试样例 12：包含空字符串的句子列表", Arrays.asList("有效的句子内容。", "", null, "有效的句子内容。"), 4, Arrays.asList("有效的句子内容。", "", null, "")),
                new TestCase("测试样例 13：极长的句子", Arrays.asList("这是一个非常长的句子，用于测试算法在处理大量内容时的性能表现。它包含许多不同的子串，以确保算法能够正确地检测和删除重复内容。通过这个测试，我们可以验证算法的效率和准确性。", "算法在处理大量内容时，能够保持高效的性能表现。它需要检测并删除所有重复的子串，以确保最终结果的准确性。这是一个关键的测试用例，用于验证算法的鲁棒性。"), 4, Arrays.asList("这是一个非常长的句子，用于测试算法在处理大量内容时的性能表现。它包含许多不同的子串，以确保算法能够正确地检测和删除重复内容。通过这个测试，我们可以验证算法的效率和准确性。", "，能够保持高效需要检测并删除所有重复最终结果的关键的测试用例，用于鲁棒性。")),
                new TestCase("测试样例 13：极长的句子", Arrays.asList("""
                        高效性：利用Aho-Corasick自动机实现了高效的多模式匹配，能够快速处理大量的子串和句子。
                        准确性：在多种重复情境下，算法能够准确识别并删除重复子串，保留句子的符号和空格。""", """
                        高效性：利用Aho-Corasick自动机实现了高效的多模式匹配，能够快速处理大量的子串和句子。
                        准确性：在多种重复情境下，算法能够准确识别并删除重复子串，保留句子的符号和空格。
                        鲁棒性：算法能够处理各种边界情况，如空句子列表、包含空字符串的句子列表以及极长的句子，确保在不同输入下的稳定性。
                        灵活性：通过调整minSubstringLength参数，算法能够适应不同的重复检测需求，增强了其应用的广泛性。"""), 10, Arrays.asList("""
                        高效性：利用Aho-Corasick自动机实现了高效的多模式匹配，能够快速处理大量的子串和句子。
                        准确性：在多种重复情境下，算法能够准确识别并删除重复子串，保留句子的符号和空格。""", """
                        鲁棒性：算法能够处理各种边界情况，如空句子列表、包含空字符串的句子列表以及极长的句子，确保在不同输入下的稳定性。
                        灵活性：通过调整minSubstringLength参数，算法能够适应不同的重复检测需求，增强了其应用的广泛性。"""))
        );
        int totalTests = testCases.size();
        int passedTests = 0;

        for (TestCase testCase : testCases) {
            System.out.println("==== " + testCase.name + " ====");
            List<String> result = removeDuplicates(testCase.sentences, testCase.minSubstringLength);
            System.out.println("预期输出:\n");
            for (int i = 0; i < testCase.expectedOutput.size(); i++) {
                System.out.println("句子" + (i + 1) + ": " + testCase.expectedOutput.get(i));
            }
            System.out.println("实际输出:\n");
            for (int i = 0; i < result.size(); i++) {
                System.out.println("句子" + (i + 1) + ": " + result.get(i));
            }

            System.out.println();

            // 对比预期与实际输出
            boolean passed = testCase.expectedOutput.size() == result.size();
            if (passed) {
                for (int i = 0; i < testCase.expectedOutput.size(); i++) {
                    String expected = testCase.expectedOutput.get(i);
                    String actual = result.get(i);
                    if ((expected == null && actual != null) || (expected != null && !expected.equals(actual))) {
                        passed = false;
                        break;
                    }
                }
            }

            System.out.println("测试结果: " + (passed ? "通过" : "失败"));
            System.out.println("==== " + testCase.name + " ====");
            System.out.println();

            System.out.println();
            if (passed) {
                passedTests++;
            }
        }
        // 输出总体结果
        System.out.println("测试完成！");
        System.out.println("共 " + totalTests + " 个测试用例，其中通过 " + passedTests + " 个，失败 " + (totalTests - passedTests) + " 个。");
    }

    /**
     * 删除多个句子中与其他句子重复的部分，同时保留每个句子的所有符号和空格。
     *
     * @param sentences          句子列表
     * @param minSubstringLength 最小子串长度
     * @return 删除重复部分后的句子列表
     */
    public static List<String> removeDuplicates(List<String> sentences, int minSubstringLength) {
        if (sentences == null || sentences.isEmpty()) {
            return sentences;
        }

        // 第一步：提取所有句子的子串，并构建模式串到句子索引的映射
        Map<String, Set<Integer>> patternToSentenceIndices = new HashMap<>();
        List<Set<String>> sentenceSubstrings = new ArrayList<>();

        for (int idx = 0; idx < sentences.size(); idx++) {
            String sentence = sentences.get(idx);
            Set<String> substrings = extractSubstrings(sentence, minSubstringLength, 100);
            sentenceSubstrings.add(substrings);
            for (String substr : substrings) {
                patternToSentenceIndices.computeIfAbsent(substr, k -> new HashSet<>()).add(idx);
            }
        }

        // 第二步：识别出现在多个句子中的重复模式串
        Set<String> duplicatePatterns = new HashSet<>();
        for (Map.Entry<String, Set<Integer>> entry : patternToSentenceIndices.entrySet()) {
            if (entry.getValue().size() > 1) {
                duplicatePatterns.add(entry.getKey());
            }
        }

        if (duplicatePatterns.isEmpty()) {
            return sentences;
        }

        // 第三步：构建只包含重复模式串的 Aho-Corasick 自动机
        AhoCorasickAutomaton automaton = new AhoCorasickAutomaton();
        for (String pattern : duplicatePatterns) {
            automaton.addPattern(pattern);
        }
        automaton.buildFailPointers();

        // 第四步：标记每个句子中需要删除的字符位置
        List<boolean[]> toDeleteList = new ArrayList<>();
        for (String sentence : sentences) {
            if (sentence == null || sentence.isEmpty()) {
                toDeleteList.add(null); // 使用 null 表示无需删除
            } else {
                toDeleteList.add(new boolean[sentence.length()]);
            }
        }

        // 遍历每个句子，查找并标记需要删除的位置
        for (int currentIdx = 0; currentIdx < sentences.size(); currentIdx++) {
            String sentence = sentences.get(currentIdx);
            if (sentence == null || sentence.isEmpty()) {
                continue;
            }

            List<AhoCorasickAutomaton.MatchResult> matches = automaton.search(sentence);
            boolean[] toDelete = toDeleteList.get(currentIdx);
            if (toDelete == null) {
                continue;
            }

            for (AhoCorasickAutomaton.MatchResult match : matches) {
                Set<Integer> sourceIndices = patternToSentenceIndices.get(match.pattern);
                if (sourceIndices == null) continue;

                // 找到模式串的最低优先级句子（即第一个出现的句子）
                int minIndex = Collections.min(sourceIndices);

                if (currentIdx != minIndex) {
                    // 仅在当前句子不是最优先级句子时，标记删除
                    for (int pos = match.start; pos < match.end; pos++) {
                        if (pos >= 0 && pos < toDelete.length) { // 防止越界
                            toDelete[pos] = true;
                        }
                    }
                }
            }
        }

        // 第五步：构建删除后的句子（保留符号和空格）
        List<String> resultSentences = new ArrayList<>();
        for (int i = 0; i < sentences.size(); i++) {
            String sentence = sentences.get(i);
            if (sentence == null || sentence.isEmpty()) {
                resultSentences.add(sentence);
                continue;
            }
            boolean[] toDelete = toDeleteList.get(i);
            if (toDelete == null) { // 额外的null检查，确保不会抛出异常
                resultSentences.add(sentence);
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < sentence.length(); j++) {
                if (!toDelete[j]) {
                    sb.append(sentence.charAt(j));
                }
            }
            // 清理多余的空格
            String cleaned = sb.toString().replaceAll("\\s{2,}", " ").trim();
            resultSentences.add(cleaned);
        }

        return resultSentences;
    }

    /**
     * 提取字符串中所有长度 >= minLen且不超过 maxLen 的子串。
     *
     * @param s      输入字符串
     * @param minLen 最小子串长度
     * @param maxLen 最大子串长度
     * @return 子串集合
     */
    private static Set<String> extractSubstrings(String s, int minLen, int maxLen) {
        Set<String> substrings = new HashSet<>();
        if (s == null || s.length() < minLen) {
            return substrings;
        }
        int len = s.length();
        maxLen = Math.min(maxLen, len); // 防止超出字符串长度

        for (int l = minLen; l <= maxLen; l++) { // 从短到长，避免过多的子串
            for (int i = 0; i <= len - l; i++) {
                String substr = s.substring(i, i + l);
                substrings.add(substr);
            }
        }
        return substrings;
    }

    /**
     * Aho-Corasick自动机类，用于高效多模式匹配。
     */
    static class AhoCorasickAutomaton {
        private final Node root;

        public AhoCorasickAutomaton() {
            root = new Node();
        }

        /**
         * 添加一个模式串。
         *
         * @param pattern 模式串
         */
        public void addPattern(String pattern) {
            if (pattern == null || pattern.isEmpty()) {
                return; // 忽略空模式串
            }
            Node current = root;
            for (char ch : pattern.toCharArray()) {
                current = current.children.computeIfAbsent(ch, c -> new Node());
            }
            current.output.add(pattern);
        }

        /**
         * 构建失败指针。
         */
        public void buildFailPointers() {
            Queue<Node> queue = new LinkedList<>();
            // 初始化第一层的失败指针
            for (Node child : root.children.values()) {
                child.fail = root;
                queue.offer(child);
            }

            // 广度优先遍历构建失败指针
            while (!queue.isEmpty()) {
                Node current = queue.poll();
                for (Map.Entry<Character, Node> entry : current.children.entrySet()) {
                    char ch = entry.getKey();
                    Node child = entry.getValue();

                    Node failNode = current.fail;
                    while (failNode != null && !failNode.children.containsKey(ch)) {
                        failNode = failNode.fail;
                    }
                    child.fail = (failNode != null) ? failNode.children.get(ch) : root;
                    child.output.addAll(child.fail.output);
                    queue.offer(child);
                }
            }
        }

        /**
         * 在文本中搜索所有模式串的匹配位置。
         *
         * @param text 文本字符串
         * @return 匹配结果列表
         */
        public List<MatchResult> search(String text) {
            List<MatchResult> results = new ArrayList<>();
            if (text == null || text.isEmpty()) {
                return results;
            }
            Node current = root;
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                while (current != root && !current.children.containsKey(ch)) {
                    current = current.fail;
                }
                if (current.children.containsKey(ch)) {
                    current = current.children.get(ch);
                }

                for (String pattern : current.output) {
                    int start = i - pattern.length() + 1;
                    int end = i + 1;
                    if (start >= 0) { // 确保索引有效
                        results.add(new MatchResult(start, end, pattern));
                    }
                }
            }
            return results;
        }

        /**
         * 匹配结果类，记录匹配的位置和模式串。
         */
        static class MatchResult {
            int start;
            int end;
            String pattern;

            MatchResult(int start, int end, String pattern) {
                this.start = start;
                this.end = end;
                this.pattern = pattern;
            }
        }

        /**
         * Trie节点类。
         */
        static class Node {
            Map<Character, Node> children = new HashMap<>();
            Node fail = null;
            List<String> output = new ArrayList<>();
        }
    }


}
