
import java.util.*;

public class DuplicateRemoverAC {

    public static void main(String[] args) {
        runAllTests();
    }

    public static void runAllTests() {
        testCase1();
        testCase2();
        testCase3();
        testCase4();
        testCase5();
        testCase6();
        testCase7();
        testCase8();
        testCase9();
    }

    // 测试案例1：普通重复
    private static void testCase1() {
        String sentence1 = "今天的天气真好，我们一起去公园散步吧。";
        String sentence2 = "我也觉得今天的天气真好，我们一起去公园散步吧。";
        String expected = "我也觉得";
        int minSubstringLength = 2; // 设置最小子串长度
        runTest(1, sentence1, sentence2, minSubstringLength, expected);
    }

    // 测试案例2：无重复
    private static void testCase2() {
        String sentence1 = "学习编程很有趣。";
        String sentence2 = "学习编程很有趣，今天下午去打篮球。";
        String expected = "，今天下午去打篮球。";
        int minSubstringLength = 2;
        runTest(2, sentence1, sentence2, minSubstringLength, expected);
    }

    // 测试案例3：完全重复
    private static void testCase3() {
        String sentence1 = "完全重复的内容。";
        String sentence2 = "完全重复的内容。";
        String expected = "";
        int minSubstringLength = 2;
        runTest(3, sentence1, sentence2, minSubstringLength, expected);
    }

    // 测试案例4：重复子串重叠
    private static void testCase4() {
        String sentence1 = "ababab";
        String sentence2 = "ababababa";
        String expected = ""; // 预期输出改为""
        int minSubstringLength = 2;
        runTest(4, sentence1, sentence2, minSubstringLength, expected);
    }

    // 测试案例5：特殊字符
    private static void testCase5() {
        String sentence1 = "咖啡馆 & 餐厅 @ 下午5点！";
        String sentence2 = "欢迎光临咖啡馆 & 餐厅 @ 下午5点！享受您的时光。";
        String expected = "欢迎光临享受您的时光。";
        int minSubstringLength = 2;
        runTest(5, sentence1, sentence2, minSubstringLength, expected);
    }

    // 测试案例6：不区分大小写
    private static void testCase6() {
        String sentence1 = "你好世界";
        String sentence2 = "你好世界！欢迎来到编程世界。";
        String expected = "！欢迎来到编程世界。";
        int minSubstringLength = 4; // 为了匹配"你好世界"（长度为4）
        runTest(6, sentence1, sentence2, minSubstringLength, expected);
    }

    // 测试案例7：sentence1为空
    private static void testCase7() {
        String sentence1 = "";
        String sentence2 = "这句话中没有任何重复内容需要删除。";
        String expected = "这句话中没有任何重复内容需要删除。";
        int minSubstringLength = 2;
        runTest(7, sentence1, sentence2, minSubstringLength, expected);
    }

    // 测试案例8：sentence2为空
    private static void testCase8() {
        String sentence1 = "任何内容";
        String sentence2 = "";
        String expected = "";
        int minSubstringLength = 2;
        runTest(8, sentence1, sentence2, minSubstringLength, expected);
    }

    // 测试案例9：多个不同重复
    private static void testCase9() {
        String sentence1 = "快速的棕色狐狸";
        String sentence2 = "这只快速的棕色狐狸跳过了懒狗。另一只快速的棕色狐狸出现了。";
        String expected = "这只跳过了懒狗。另一只出现了。";
        int minSubstringLength = 2;
        runTest(9, sentence1, sentence2, minSubstringLength, expected);
    }



    // 通用测试运行方法
    private static void runTest(int testCaseNumber, String sentence1, String sentence2, int minSubstringLength, String expected) {
        String result = removeDuplicates(sentence1, sentence2, minSubstringLength);
        boolean passed = result.equals(expected);
        System.out.println("测试案例 " + testCaseNumber + ": " + (passed ? "通过" : "失败"));
        if (!passed) {
            System.out.println("预期输出: \"" + expected + "\"");
            System.out.println("实际输出: \"" + result + "\"");
        }
        System.out.println("-------------------------------------");
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
