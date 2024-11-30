
import java.util.*;

public class ACDuplicateRemover {

    /**
     * 测试用例类
     */
    static class TestCase {
        String name;
        String sentence1;
        String sentence2;
        int minSubstringLength;
        String expectedOutput;

        public TestCase(String name, String sentence1, String sentence2, int minSubstringLength, String expectedOutput) {
            this.name = name;
            this.sentence1 = sentence1;
            this.sentence2 = sentence2;
            this.minSubstringLength = minSubstringLength;
            this.expectedOutput = expectedOutput;
        }
    }

    public static void main(String[] args) {
        List<TestCase> testCases = List.of(
                new TestCase("测试案例1：普通重复", "今天的天气真好，我们一起去公园散步吧。", "我也觉得今天的天气真好，我们一起去公园散步吧。", 2, "我也觉得"),
                new TestCase("测试案例2：无重复", "学习编程很有趣。", "学习编程很有趣，今天下午去打篮球。", 2, "，今天下午去打篮球。"),
                new TestCase("测试案例3：完全重复", "完全重复的内容。", "完全重复的内容。", 2, ""),
                new TestCase("测试案例4：重复子串重叠", "ababab", "ababababa", 2, ""),
                new TestCase("测试案例5：特殊字符", "咖啡馆 & 餐厅 @ 下午5点！", "欢迎光临咖啡馆 & 餐厅 @ 下午5点！享受您的时光。", 2, "欢迎光临享受您的时光。"),
                new TestCase("测试案例6：不区分大小写", "你好世界", "你好世界！欢迎来到编程世界。", 4, "！欢迎来到编程世界。"),
                new TestCase("测试案例7：sentence1为空", "", "这句话中没有任何重复内容需要删除。", 2, "这句话中没有任何重复内容需要删除。"),
                new TestCase("测试案例8：sentence2为空", "任何内容", "", 2, ""),
                new TestCase("测试案例9：多个不同重复", "快速的棕色狐狸", "这只快速的棕色狐狸跳过了懒狗。另一只快速的棕色狐狸出现了。", 2, "这只跳过了懒狗。另一只出现了。")
        );
        int totalTests = testCases.size();
        int passedTests = 0;

        // 使用for循环遍历所有测试用例并执行
        for (TestCase testCase : testCases) {
            System.out.println("==== " + testCase.name + " ====");
            String result = removeDuplicates(testCase.sentence1, testCase.sentence2, testCase.minSubstringLength);
            boolean passed = result.equals(testCase.expectedOutput);
            System.out.println("预期输出: \n" + testCase.expectedOutput);
            System.out.println("实际输出: \n" + result);
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
     * 删除第二个句子中与第一个句子重复的部分，同时保留第二个句子的所有符号和空格。
     *
     * @param sentence1          第一个句子
     * @param sentence2          第二个句子
     * @param minSubstringLength 最小子串长度
     * @return 删除重复部分后的第二个句子
     */
    public static String removeDuplicates(String sentence1, String sentence2, int minSubstringLength) {
        if (sentence1 == null || sentence2 == null || sentence2.isEmpty()) {
            return sentence2;
        }

        // 提取sentence1中所有长度 >= minSubstringLength的子串
        List<String> patterns = extractSubstrings(sentence1, minSubstringLength, 100); // 设置最大子串长度为100

        if (patterns.isEmpty()) {
            return sentence2;
        }

        // 构建Aho-Corasick自动机
        AhoCorasickAutomaton automaton = new AhoCorasickAutomaton();
        for (String pattern : patterns) {
            automaton.addPattern(pattern);
        }
        automaton.buildFailPointers();

        // 查找所有匹配的模式串及其位置
        List<MatchResult> matches = automaton.search(sentence2);

        if (matches.isEmpty()) {
            return sentence2;
        }

        // 标记需要删除的字符位置
        boolean[] toDelete = new boolean[sentence2.length()];

        for (MatchResult match : matches) {
            for (int i = match.start; i < match.end; i++) {
                toDelete[i] = true;
            }
        }

        // 构建删除后的字符串（不插入空格）
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentence2.length(); i++) {
            if (!toDelete[i]) {
                sb.append(sentence2.charAt(i));
            }
        }

        // 清理多余的空格
        String result = sb.toString().replaceAll("\\s{2,}", " ").trim();

        return result;
    }

    /**
     * 提取字符串中所有长度 >= minLen且不超过 maxLen 的子串。
     *
     * @param s      输入字符串
     * @param minLen 最小子串长度
     * @param maxLen 最大子串长度
     * @return 子串列表
     */
    private static List<String> extractSubstrings(String s, int minLen, int maxLen) {
        Set<String> substrings = new HashSet<>();
        int len = s.length();
        maxLen = Math.min(maxLen, len); // 防止超出字符串长度

        for (int l = minLen; l <= maxLen; l++) { // 从短到长，避免过多的子串
            for (int i = 0; i <= len - l; i++) {
                String substr = s.substring(i, i + l);
                substrings.add(substr);
            }
        }
        return new ArrayList<>(substrings);
    }

    /**
     * 匹配结果类
     */
    static class MatchResult {
        int start;
        int end;

        MatchResult(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    /**
     * Aho-Corasick自动机类
     */
    static class AhoCorasickAutomaton {
        private final Node root;

        public AhoCorasickAutomaton() {
            root = new Node();
        }

        /**
         * 添加一个模式串
         *
         * @param pattern 模式串
         */
        public void addPattern(String pattern) {
            Node current = root;
            for (char ch : pattern.toCharArray()) {
                current = current.children.computeIfAbsent(ch, c -> new Node());
            }
            current.output.add(pattern);
        }

        /**
         * 构建失败指针
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
         * 在文本中搜索所有模式串的匹配位置
         *
         * @param text 文本字符串
         * @return 匹配结果列表
         */
        public List<MatchResult> search(String text) {
            List<MatchResult> results = new ArrayList<>();
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
                    results.add(new MatchResult(start, end));
                }
            }
            return results;
        }

        /**
         * Trie节点类
         */
        static class Node {
            Map<Character, Node> children = new HashMap<>();
            Node fail = null;
            List<String> output = new ArrayList<>();
        }
    }
}
