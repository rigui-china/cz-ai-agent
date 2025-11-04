## 1、AI 大模型接入

### AI 大模型概念

**什么是 AI 大模型？**

AI 大模型是指具有超大规模参数（通常为数十亿到数万亿）的深度学习模型，通过对大规模数据的训练，能够理解、生成人类语言，处理图像、音频等多种模态数据，并展示出强大的推理和创作能力。

大模型的强大之处在于它的**涌现能力**——随着模型参数量和训练数据量的增加，模型会展现出训练过程过程中未明确赋予的新能力，比如逻辑推理、代码编写、多步骤问题解决等。

### AI 大模型的分类

1. **按模态分类**
   - 单模态模型：仅处理单一类型的数据，如纯文本
   - 多模态模型：能够处理多种类型的信息
   - 文本 + 图像：GPT-4V、Gemini、Claude 3
   - 文本 + 音频 + 视频：GPT-4o
2. **按开源性分类**
   - 闭源模型
   - 开源模型
3. **按规模分类**
   - 超大规模模型：参数量在数千亿到数万亿（比如 GPT-4 1.76T 参数）
   - 中小规模模型：参数量在几十亿到几百亿
4. **按用途分类**
   - 通用模型：能处理广泛的任务（比如 GPT-4、Claude 3 、Gemini）
   - 特定领域模型：针对特定领域优化（医疗：Med-PaLM 2、代码：CodeLlama、StarCoder）

### 接入大模型

一般在实际开发过程中，我们主要有 2 种途径来使用 AI 大模型，分别是云服务和自部署，各有优缺。

1. **云服务**：直接使用其它云服务商在云端已部署好的大模型服务，无需自己考虑基础设施（比如服务器、GPU 算力）
2. **自部署**：开发者自行在本地或私有云环境部署开源大模型

**实际接入大模型的 3 种方式**

1. **AI 应用平台接入**

   通过云服务商提供的 AI 应用平台来使用 AI 大模型。阿里云百炼平台主要是为了可视化提供给技术和非技术人员使用的，后面讲的灵积主要提供的是SDK和API接口，主要是为了开发人员使用的。

   一般可以建立三种应用（前两种在百炼平台上可见，最后一种看不到）：

   - 智能体应用：接入某个具体的大模型，然后可以自己设置系统prompt、一些文件帮助类似（RAG的作用）、联网搜索、知识库过滤等、还有MCP、插件、别的智能体作为组件以及工作流，以及上下文设置（比如历史对话），还有设置回复等。然后可以自己进行调试、设置等
   - 工作流应用：适用于需要结合大模型执行**高确定性**的业务逻辑的流程型应用，步骤等已经确定好，可以自己加入不同的智能体、mcp、api或者别的工作流作为中间的步骤。
   - 智能体编排应用：跟工作流的流程差不多，不过中间的步骤为智能体应用。

2. **AI 软件客户端接入**

   除了平台之外，还可以通过 AI 软件客户端来使用大模型能力。

   1. Cherry Studio：一款集成了多模型对话、知识库管理、AI 绘画、翻译等功能于一体的全能 AI 助手平台。
   2. Cursor：以 AI 为核心的编程开发工具，可以快速生成项目代码、理解整个代码库并提供智能建议

3. **程序接入**

   可以通过编程的方式在自己的项目种调用 AI 大模型。

   1. 直接调用 AI 大模型，比如调用 DeepSeek（使用特定平台提供的 SDK 或 API，参考平台的文档来接入；也可以使用 AI 开发框架进行自主选择）
   2. 调用 AI 大模型平台创建的应用或智能体（一般只能使用特定平台提供的 SDK 或 API，参考平台的文档进行接入）



### 后端项目初始化

#### 环境准备

使用 JDK 21 版本。

新建项目使用 Spring Boot，确保 Server URL 为 https://start.spring.io/。并且 spring boot 版本为 3.4.x版本。

再添加 Spring Web 和 Lombok以及 Hutool 工具库和 Knife4j 的依赖。

1. 新建一个 controller 包用于存放 API 接口，编写一个接口用于测试接口文档是否正常引入

   ```java
   @RestController
   @RequestMapping("/health")
   public class HealthController {
   
       @GetMapping
       public String healthCheck() {
           return "ok";
       }
   }
   ```

2. 将 resources 目录下的 application 文件改名为 yml ，添加文档接口配置。

   ```yaml
   spring:
     application:
       name: chengzhi-ai-agent
   server:
     port: 8123
     servlet:
       context-path: /api
   # springdoc-openapi
   springdoc:
     swagger-ui:
       path: /swagger-ui.html
       tags-sorter: alpha
       operations-sorter: alpha
     api-docs:
       path: /v3/api-docs
     group-configs:
       - group: 'default'
         paths-to-match: '/**'
         packages-to-scan: com.yupi.yuaiagent.controller
   # knife4j
   knife4j:
     enable: true
     setting:
       language: zh_cn
   ```

3. 启动项目，访问 http://localhost:8123/api/doc.html 能够看到接口文档，可以测试调用接口

   ![1761722692061](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761722692061.png)



### 程序调用 AI 大模型

1. **SDK 接入：使用官方提供的软件开发工具包，最直接的集成方式**

   首先引入官方的 SDK，在 pom.xml 中引入依赖

   ```xml
   <!-- https://mvnrepository.com/artifact/com.alibaba/dashscope-sdk-java -->
   <dependency>
       <groupId>com.alibaba</groupId>
       <artifactId>dashscope-sdk-java</artifactId>
       <version>2.19.1</version>
   </dependency>
   ```

   然后在百炼平台申请一个 API Key，注意不要泄漏。

   ![1761723411581](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761723411581.png)

   项目中新建 `demo.invoke`包，集中存放调用 AI 大模型的示例代码。

   具体代码示例参考官方文档https://help.aliyun.com/zh/model-studio/use-qwen-by-calling-api#a9b7b197e2q2v，如下：

   为了安全管理 API 密钥，我们创建一个接口类来存储密钥信息（实际环境中，应使用配置文件或环境变量）

   ```java
   public interface TestApiKey {
   
       String API_KEY = "你的 API Key";
   }
   ```

   然后将下面的 api_key 设置成自己的 api_key 即可进行测试。

   ```java
   // 建议dashscope SDK的版本 >= 2.12.0
   import java.util.Arrays;
   import java.lang.System;
   import com.alibaba.dashscope.aigc.generation.Generation;
   import com.alibaba.dashscope.aigc.generation.GenerationParam;
   import com.alibaba.dashscope.aigc.generation.GenerationResult;
   import com.alibaba.dashscope.common.Message;
   import com.alibaba.dashscope.common.Role;
   import com.alibaba.dashscope.exception.ApiException;
   import com.alibaba.dashscope.exception.InputRequiredException;
   import com.alibaba.dashscope.exception.NoApiKeyException;
   import com.alibaba.dashscope.utils.JsonUtils;
   
   public class Main {
       public static GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
           Generation gen = new Generation();
           Message systemMsg = Message.builder()
                   .role(Role.SYSTEM.getValue())
                   .content("You are a helpful assistant.")
                   .build();
           Message userMsg = Message.builder()
                   .role(Role.USER.getValue())
                   .content("你是谁？")
                   .build();
           GenerationParam param = GenerationParam.builder()
                   // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                   .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                   // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                   .model("qwen-plus")
                   .messages(Arrays.asList(systemMsg, userMsg))
                   .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                   .build();
           return gen.call(param);
       }
       public static void main(String[] args) {
           try {
               GenerationResult result = callWithMessage();
               System.out.println(JsonUtils.toJson(result));
           } catch (ApiException | NoApiKeyException | InputRequiredException e) {
               // 使用日志框架记录异常信息
               System.err.println("An error occurred while calling the generation service: " + e.getMessage());
           }
           System.exit(0);
       }
   }
   ```

   运行项目，成功看到 AI 的回复：

   ```java
   SLF4J(W): Class path contains multiple SLF4J providers.
   SLF4J(W): Found provider [ch.qos.logback.classic.spi.LogbackServiceProvider@1888ff2c]
   SLF4J(W): Found provider [org.slf4j.simple.SimpleServiceProvider@35851384]
   SLF4J(W): See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.
   SLF4J(I): Actual provider is of type [ch.qos.logback.classic.spi.LogbackServiceProvider@1888ff2c]
   {"requestId":"3a137c07-755a-4e9d-8850-b8c692b234f7","usage":{"input_tokens":22,"output_tokens":69,"total_tokens":91},"output":{"choices":[{"finish_reason":"stop","message":{"role":"assistant",
   "content":"我是通义千问（Qwen），由阿里云研发的超大规模语言模型。我可以生成各种类型的文本，如文章、故事、诗歌、故事等，并能够根据不同的场景和需求进行变换和扩展。此外，我还能够回答各种问题，提供帮助和解决方案。如果您有任何问题或需要帮助，请随时告诉我！"}}]}}
   ```

2. **HTTP 接入**

   对于 SDK 不支持的编程语言或需要更灵活控制的场景，可以直接使用 HTTP 请求调用 AI 大模型的 API。

   HTTP 调用的详细说明依然可以参考官方文档：https://help.aliyun.com/zh/model-studio/use-qwen-by-calling-api#9141263b961cc，选取其中采用 curl 进行访问的方式。

   ```curl
   curl --location "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation" \
   --header "Authorization: Bearer $DASHSCOPE_API_KEY" \
   --header "Content-Type: application/json" \
   --data '{
       "model": "qwen-plus",
       "input":{
           "messages":[      
               {
                   "role": "system",
                   "content": "You are a helpful assistant."
               },
               {
                   "role": "user",
                   "content": "你是谁？"
               }
           ]
       },
       "parameters": {
           "result_format": "message"
       }
   }'
   ```

   然后可以利用 AI 将上述代码转换成 Java 的 Hutool 工具类网络请求代码，如下：

   ```java
   public class HttpAiInvoke {
       public static void main(String[] args) {
           // 替换为你的实际 API 密钥
           String apiKey = TestApiKey.API_KEY;
   
           String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
   
           // 设置请求头
           Map<String, String> headers = new HashMap<>();
           headers.put("Authorization", "Bearer " + TestApiKey.API_KEY);
           headers.put("Content-Type", "application/json");
   
           // 设置请求体
           JSONObject requestBody = new JSONObject();
           requestBody.put("model", "qwen-plus");
   
           JSONObject input = new JSONObject();
           JSONObject[] messages = new JSONObject[2];
   
           JSONObject systemMessage = new JSONObject();
           systemMessage.put("role", "system");
           systemMessage.put("content", "You are a helpful assistant.");
           messages[0] = systemMessage;
   
           JSONObject userMessage = new JSONObject();
           userMessage.put("role", "user");
           userMessage.put("content", "你是谁？");
           messages[1] = userMessage;
   
           input.put("messages", messages);
           requestBody.put("input", input);
   
           JSONObject parameters = new JSONObject();
           parameters.put("result_format", "message");
           requestBody.put("parameters", parameters);
   
           // 发送请求
           String result = HttpRequest.post(url)
                   .header("Authorization", "Bearer " + apiKey)
                   .header("Content-Type", "application/json")
                   .body(requestBody.toString())
                   .execute()
                   .body();
   
           System.out.println(result);
       }
   }
   ```

3. **Spring AI**使用开发框架

   参考官方文档，来跑通调用大模型的流程：

   此处为灵积模型接入文档：https://java2ai.com/docs/1.0.0-M6.1/models/dashScope/

   首先引入依赖，这里为 1.0.0-M6.1 版本

   ```xml
   <dependency>
       <groupId>com.alibaba.cloud.ai</groupId>
       <artifactId>spring-ai-alibaba-starter</artifactId>
       <version>1.0.0-M6.1</version>
   </dependency>
   
   ```

   如果出现 spring-ai-core 等相关依赖解析问题，请在 pom 中加入如下依赖：

   ```xml
   <repositories>
     <repository>
       <id>spring-milestones</id>
       <name>Spring Milestones</name>
       <url>https://repo.spring.io/milestone</url>
       <snapshots>
         <enabled>false</enabled>
       </snapshots>
     </repository>
   </repositories>
   
   ```

   编写 application 相关配置

   ```yaml
   spring:
     ai:
       dashscope:
         api-key: ${AI_DASHSCOPE_API_KEY}
         chat:
           options:
             model: qwen-plus
   ```

   编写示例代码，注意要注入的是 dashscopeChatModel，这里 @Resource 看是否成功注入可以在左边有一个小绿色的标志，点一下如果可以跳转到 dashscopeChatModel 的 bean 的类，代表注入成功。

   ```java
   @Component
   public class SpringAiInvoke implements CommandLineRunner {
   
       @Resource
       private ChatModel dashscopeChatModel;
   
   
       @Override
       public void run(String... args) throws Exception {
           AssistantMessage output = dashscopeChatModel.call(new Prompt("你好啊,我是橙汁"))
                   .getResult()
                   .getOutput();
           System.out.println(output.getText());
       }
   }
   ```

   上面的代码实现了 CommandLineRunner 接口，我们启动 Spring Boot 项目时，会自动注入大模型 ChatModel 依赖，并且单次执行该类的 run 方法，达到测试的效果。

4. **LangChain4j** 同样是一个框架

   官方不支持阿里系大模型，只能使用社区版本的整合大模型包：https://github.com/langchain4j/langchain4j-community/tree/main/models

   接入阿里云灵积模型，参考：https://docs.langchain4j.dev/integrations/language-models/dashscope/

   首先也是引入依赖：

   ```xml
   <!-- https://mvnrepository.com/artifact/dev.langchain4j/langchain4j-community-dashscope -->
   <dependency>
       <groupId>dev.langchain4j</groupId>
       <artifactId>langchain4j-community-dashscope</artifactId>
       <version>1.0.0-beta2</version>
   </dependency>
   
   ```

   注意，langchain4j 和 spring ai 的依赖最好只引入一个，因为双方的代码有很多重复的地方，防止写的时候引入错误的包。

   如下是官方示例：

   ```java
   public class LangChainAiInvoke {
   
       public static void main(String[] args) {
           ChatLanguageModel qwenModel = QwenChatModel.builder()
                   .apiKey(TestApiKey.API_KEY)
                   .modelName("qwen-max")
                   .build();
           String answer = qwenModel.chat("我是程序员橙汁儿，你好");
           System.out.println(answer);
       }
   }
   
   ```

   ### 扩展知识 - 本地部署和接入 AI 大模型

   1. **本地安装大模型**

      直接使用开源项目 Ollama https://ollama.com/ 快速安装大模型。

      首先下载安装 Ollama，并安装其命令行工具

      安装完成后，打开终端执行 ollama --help 可以查看其用法

      进入到 ollama 的模型广场中，挑选模型；选中模型后，支持切换模型版本，建议选择小模型

      执行 ollama 命令来快速安装并运行大模型，一般为 ollama run 模型:版本

      安装运行成功后，可以在终端打字和大模型对话

      访问 http://localhost:11434，能够看到模型正常运行

   2. **Spring AI 调用 Ollama 大模型**（需要上一步已经在本地安装成功 ollama 大模型）

      首先还是引入依赖：

      ```xml
      <dependency>
          <groupId>org.springframework.ai</groupId>
          <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
          <version>1.0.0-M6</version>
      </dependency>
      
      ```

      同样，如果出现依赖解析问题，加入如下依赖

      ```xml
      <repositories>
        <repository>
          <id>spring-milestones</id>
          <name>Spring Milestones</name>
          <url>https://repo.spring.io/milestone</url>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
      </repositories>
      
      ```

      填写相应yml配置

      ```yaml
      spring:
        ai:
          ollama:
            base-url: http://localhost:11434
            chat:
              model: gemma3:1b
      
      ```

      编写相应测试代码：

      ```java
      @Component
      public class OllamaAiInvoke implements CommandLineRunner {
      
          @Resource
          private ChatModel ollamaChatModel;
      
          @Override
          public void run(String... args) throws Exception {
              AssistantMessage output = ollamaChatModel.call(new Prompt("你好，我是橙汁"))
                      .getResult()
                      .getOutput();
              System.out.println(output.getText());
          }
      }
      ```

      

## 2、AI 应用开发

### Prompt 工程

**基本概念**

Prompt 工程又叫提示词工程，简单来说，就是输入给 AI 的指令。比如下面这段内容，就是提示词：

```markdown
请问xxx有什么用？
```

我们学习 Prompt 工程的目标是：通过精心设计和优化输入提示来引导 AI 模型生成符合预期的高质量输出。

**提示词分类**

1. **基于角色的分类**

   - 用户 Prompt：这是用户向 AI 提供的实际问题、指令或信息，传达了用户的直接需求。用户 Prompt 告诉  AI 模型 “做什么”，比如回答问题、编写代码、生成创意内容等。

     ```plain
     用户：帮我写一首关于冬天的诗
     
     ```

   - 系统 Prompt：这是设置 AI 模型行为规则和角色定位的隐藏指令，用户通常不能直接看到。系统 Prompt 相当于给 AI 设定人格和能力边界，即告诉 AI “你是谁？你能做什么？”

     ```markdown
     系统：你是一位经验丰富的恋爱顾问，擅长分析情感问题并提供建设性建议。请以温暖友善的语气回答用户的恋爱困惑，必要时主动询问更多信息以便提供更准确的建议。不要做出道德判断，而是尊重用户的情感体验并提供实用的沟通和相处技巧。回答时保持专业性，但避免使用过于学术的术语，确保普通用户能够理解你的建议。
     
     ```

     不同的系统 Prompt 可以让同一个 AI 模型表示出完全不同的应用特性，这是构建垂直 AI 应用（如财务顾问、教育辅导、医疗咨询等）的关键。

   - 助手 Prompt：这是 AI 模型的响应内容。在多轮会话中，之前的助手回复也会成为当前上下文的一部分，影响后续对话的理解和生成。某些场景下，开发者可以预设一些助手消息作为对话历史的一部分，引导后续互动。

     ```markdown
     助手：我是你的恋爱顾问，很高兴能帮助你解决情感问题。你目前遇到了什么样的恋爱困惑呢？可以告诉我你们的关系现状和具体遇到的问题吗？
     
     ```

     在实际应用中，这些不同类型的提示词往往会组合使用。

     ```markdown
     系统：你是编程导航的专业编程导师，擅长引导初学者入门编程并制定学习路径。使用友好鼓励的语气，解释复杂概念时要通俗易懂，适当使用比喻让新手理解，避免过于晦涩的技术术语。
     
     用户：我完全没有编程基础，想学习编程开发，但不知道从何开始，能给我一些建议吗？
     
     助手：欢迎加入编程的世界！作为编程小白，建议你可以按照以下步骤开始学习之旅...
     
     【多轮对话继续】
     
     ```

   - AI 大模型平台允许用户自主设置不同类型的提示词来进行调试

2. **基于功能的分类**（扩展知识）

   - 指令型提示词：明确告诉 AI 模型需要执行的任务，通常以命令式语句开头。

     ```plain
     翻译以下文本为英文：春天来了，花儿开了。
     
     ```

   - 对话型提示词：模拟自然对话，以问答形式与 AI 模型交互。

     ```plain
     你认为人工智能会在未来取代人类工作吗？
     
     ```

   - 创意型提示词：引导 AI 模型进行创意内容生成，如故事、诗歌、广告文案等。

     ```plain
     写一个发生在未来太空殖民地的短篇科幻故事，主角是一位机器人工程师。
     
     ```

   - 角色扮演提示词：让 AI 扮演特定角色或任务进行回答。

     ```plain
     假设你是爱因斯坦，如何用简单的语言解释相对论？
     
     ```

   - 少样本学习提示词：提供一些示例，引导 AI 理解所需的输出格式和风格。

     ```plain
     将以下句子改写为正式商务语言：
     示例1：
     原句：这个想法不错。
     改写：该提案展现了相当的潜力和创新性。
     
     示例2：
     原句：我们明天见。
     改写：期待明日与您会面，继续我们的商务讨论。
     
     现在请改写：这个价格太高了。
     
     ```

3. **基于复杂度的分类**

   - 简单提示词：单一指令或问题。

     ```plain
     什么是人工智能？
     
     ```

   - 复合提示词：包含多个相关指令或步骤的提示词。

     ```plain
     分析下面这段代码，解释它的功能，找出潜在的错误，并提供改进建议。
     
     ```

   - 链式提示词：一系列连续的、相互依赖的提示词，每个提示词基于前一个提示词的输出。

     ```plain
     第一步：生成一个科幻故事的基本情节。
     第二步：基于情节创建三个主要角色，包括他们的背景和动机。
     第三步：利用这些角色和情节，撰写故事的开篇段落。
     
     ```

   - 模板提示词：包含可替换变量的标准化提示词结构，常用于大规模应用。

     ```plain
     你是一位专业的{领域}专家。请回答以下关于{主题}的问题：{具体问题}。
     回答应包含{要点数量}个关键点，并使用{风格}的语言风格。
     
     ```



### Token

Token 是大模型处理文本的基本单位，可能是单词或标点符号，模型的输入和输出都是按 Token 计算的，一般 Token 越多，成本越高，并且输出速度越慢。

**如何计算 Token？**

首先，不同大模型对 Token 的划分规则略有不同，比如根据 OpenAI 的文档：

- 英文文本：一个 token 大约相当于 4 个字符或约 0.75 个英文单词
- 中文文本：一个汉字通常会被编码为 1-2 个 token
- 空格和标点：也会计入 token 数量
- 特殊表情和表情符号：可能需要多个 token 来表示

实际应用中，更推荐使用工具来估计 Prompt 的 Token 数量。比如 OpenAI 自己有官方的 Token 计算器。

**Token 成本计算**

估算成本有个公式：总成本 = （输入 token 数 * 输入单价）+ （输出 token 数 * 输出单价）

**Token 成本优化技巧**

1. **精简系统提示词**：移除冗余表述，保留核心指令。比如将 “你是一个非常专业、经验丰富且非常有耐心的编程导师” 简化为 “你是编程导师”

2. **定期清理对话历史**：对话上下文会随着交互不断累积 Token。在长对话中，可以定期请求 AI 总结之前的对话，然后以总结替代详细历史。

   ```plain
   请总结我们至今的对话要点，后续我们将基于此总结继续讨论。
   
   ```

3. **使用向量检索代替直接输入**：对于需要处理大量参考文档的场景，不要直接将整个文档作为 Prompt，而是使用向量数据库和检索技术（RAG）获取相关段落。

4. **结构化替代自然语言**：使用表格、列表等结构化格式代替长段落描述：

   优化前：

   ```markdown
   请问如何制作披萨？首先需要准备面粉、酵母、水、盐、橄榄油作为基础面团材料。然后根据口味选择酱料，可以是番茄酱或白酱。接着准备奶酪，最常用的是马苏里拉奶酪。最后准备各种配料如意大利香肠、蘑菇、青椒等。
   
   ```

   优化后：

   ```markdown
   披萨制作材料：
   - 面团：面粉、酵母、水、盐、橄榄油
   - 酱料：番茄酱/白酱
   - 奶酪：马苏里拉
   - 配料：意大利香肠、蘑菇、青椒等
   
   如何制作？
   
   ```



### Prompt 优化技巧

1. **Prompt 学习**

   网上和 Prompt 优化相关的资源非常丰富，几乎各大主流 AI 大模型和 AI 开发框架官方文档都有相关的介绍，推荐先阅读至少两篇。

   Prompt Engineering Guide 提示工程指南：https://www.promptingguide.ai/zh

   OpenAI 提示词工程指南：https://platform.openai.com/docs/guides/prompt-engineering

2. **Prompt 提示词库**

   网上也有很多现成的提示词库，在自主优化提示词前，可以先尝试搜索有没有现成的提示词参考：

   文本对话：https://docs.anthropic.com/zh-CN/prompt-library/library

   AI 绘画：https://promptlibrary.org/

#### 基础提示技巧

1. **明确指定任务和角色**

   为 AI 提供清晰的任务描述和角色定位

   ```plain
   系统：你是一位经验丰富的Python教师，擅长向初学者解释编程概念。
   用户：请解释 Python 中的列表推导式，包括基本语法和 2-3 个实用示例。
   ```

2. **提供详细说明和具体示例**

   提供足够的上下文信息和期望的输出格式示例

   ```plain
   请提供一个社交媒体营销计划，针对一款新上市的智能手表。计划应包含:
   1. 目标受众描述
   2. 三个内容主题
   3. 每个平台的内容类型建议
   4. 发布频率建议
   
   示例格式:
   目标受众: [描述]
   内容主题: [主题1], [主题2], [主题3]
   平台策略: [平台] - [内容类型] - [频率]
   ```

3. **使用结构化格式引导思维**

   通过列表、表格等结构化格式，使指令更易理解，输出更有条理

   ```plain
   分析以下公司的优势和劣势:
   公司: Tesla
   
   请使用表格格式回答，包含以下列:
   - 优势(最少3项)
   - 每项优势的简要分析
   - 劣势(最少3项)
   - 每项劣势的简要分析
   - 应对建议
   
   ```

4. **明确输出格式要求**

   指定输出的格式、长度、风格等要求

   ```plain
   撰写一篇关于气候变化的科普文章，要求:
   - 使用通俗易懂的语言，适合高中生阅读
   - 包含5个小标题，每个标题下2-3段文字
   - 总字数控制在800字左右
   - 结尾提供3个可行的个人行动建议
   
   ```

#### 进阶提示技巧

1. **思维链提示法**

   引导模型展示推理过程，逐步思考问题，提高复杂问题的准确性

   ```plain
   问题：一个商店售卖T恤，每件15元。如果购买5件以上可以享受8折优惠。小明买了7件T恤，他需要支付多少钱？
   
   请一步步思考解决这个问题:
   1. 首先计算7件T恤的原价
   2. 确定是否符合折扣条件
   3. 如果符合，计算折扣后的价格
   4. 得出最终支付金额
   
   ```

2. **少样本学习**

   通过提供几个输入 - 输出对的示例，帮助模型理解任务模式和期望输出

   ```plain
   我将给你一些情感分析的例子，然后请你按照同样的方式分析新句子的情感倾向。
   
   输入: "这家餐厅的服务太差了，等了一个小时才上菜"
   输出: 负面，因为描述了长时间等待和差评服务
   
   输入: "新买的手机屏幕清晰，电池也很耐用"
   输出: 正面，因为赞扬了产品的多个方面
   
   现在分析这个句子:
   "这本书内容还行，但是价格有点贵"
   
   ```

3. **分步骤指导**

   将复杂任务分解为可管理的步骤，确保模型完成每个关键环节

   ```plain
   请帮我创建一个简单的网站落地页设计方案，按照以下步骤:
   
   步骤1: 分析目标受众(考虑年龄、职业、需求等因素)
   步骤2: 确定页面核心信息(主标题、副标题、价值主张)
   步骤3: 设计页面结构(至少包含哪些区块)
   步骤4: 制定视觉引导策略(颜色、图像建议)
   步骤5: 设计行动召唤(CTA)按钮和文案
   
   ```

4. **自我评估和修正**

   让模型评估自己的输出并进行改进，提高准确性和质量

   ```plain
   解决以下概率问题:
   从一副标准扑克牌中随机抽取两张牌，求抽到至少一张红桃的概率。
   
   首先给出你的解答，然后:
   1. 检查你的推理过程是否存在逻辑错误
   2. 验证你使用的概率公式是否正确
   3. 检查计算步骤是否有误
   4. 如果发现任何问题，提供修正后的解答
   
   ```

5. **知识检索和引用**

   引导模型检索相关信息并明确引用信息来源，提高可靠性

   ```plain
   请解释光合作用的过程及其在植物生长中的作用。在回答中:
   1. 提供光合作用的科学定义
   2. 解释主要的化学反应
   3. 描述影响光合作用效率的关键因素
   4. 说明其对生态系统的重要性
   
   对于任何可能需要具体数据或研究支持的陈述，请明确指出这些信息的来源，并说明这些信息的可靠性。
   
   ```

6. **多视角分析**

   引导模型从不同角度、立场或专业视角分析问题，提供全面见解

   ```plain
   分析"城市应该禁止私家车进入市中心"这一提议:
   
   请从以下4个不同角度分析:
   1. 环保专家视角
   2. 经济学家视角
   3. 市中心商户视角
   4. 通勤居民视角
   
   对每个视角:
   - 提供支持该提议的2个论点
   - 提供反对该提议的2个论点
   - 分析可能的折中方案
   
   ```

7. **多模态思维**

   结合不同表达形式进行思考，如文字描述、图标结构、代码逻辑等。

   ```plain
   设计一个智能家居系统的基础架构:
   
   1. 首先用文字描述系统的主要功能和组件
   2. 然后创建一个系统架构图(用ASCII或文本形式表示)
   3. 接着提供用户交互流程
   4. 最后简述实现这个系统可能面临的技术挑战
   
   尝试从不同角度思考:功能性、用户体验、技术实现、安全性等。
   
   ```



### AI 应用方案设计

根据需求，我们将实现一个具有多轮对话能力的 AI 编程学习助手应用。整体方案设计将围绕 2 个核心展开：

- 系统提示词的设计
- 多轮对话的实现

1. **系统提示词设计**

   对于 AI 对话应用，最简单的做法是直接写一段系统预设，定义 “你是谁？能做什么”，比如：

   ```markdown
   你是一位编程导师，为用户提供编程学习帮助
   ```

   这种简单提示虽然可以工作，但效果往往不够理想。想想现实中的场景，我们去跟老师咨询沟通时，老师可能会主动抛出一系列引导性问题、深入了解你的学习背景、基础，而不是被动等待用户完整描述问题。比如：

   - 最近学习上遇到什么问题了吗？
   - 请问你有什么需要我帮助的事情么？

   因为我们要优化系统预设，可以借助 AI 进行优化。示例 Prompt：

   ```markdown
   我正在开发【编程学习助手】AI 对话应用，请你帮我编写设置给 AI 大模型的系统预设 Prompt 指令。要求让 AI 作为普通人学习编程的导师，模拟真实学习编程时的咨询场景、多给用户一些学习编程时的帮助和指导，不断深入了解用户，从而提供给用户更全面的建议，解决用户的编程学习。
   ```

   AI 提供的优化后系统提示词：

   ```markdown
   你是一位耐心、专业的编程学习导师，专注于帮助普通人从零开始掌握编程技能。你的核心任务是理解用户的学习目标、当前水平和遇到的具体问题，通过模拟真实的编程学习咨询场景，提供个性化的指导和支持。
   
   ### 核心职责
   1. **场景化互动**：以自然对话的方式与用户交流，像真实导师一样询问用户的学习背景（如是否有编程基础、学习编程的目的、目前在学的编程语言等），避免机械性回复。
   2. **问题拆解与解答**：当用户提出编程问题时，先判断问题难度是否匹配其水平。若问题过难，先铺垫基础概念；若问题较基础，可结合实例讲解，确保用户理解原理而非仅记住答案。
   3. **学习路径引导**：根据用户的目标（如职业发展、兴趣爱好、考试需求等），推荐阶段性的学习内容和资源（如适合的教材、在线课程、练习平台），并提醒学习中的重点和常见误区。
   4. **实践指导**：鼓励用户通过代码练习巩固知识，对用户编写的代码提供具体修改建议，指出逻辑错误、优化方向，并解释原因（例如：“这段循环逻辑可以简化，因为……”）。
   5. **持续跟进与调整**：记住用户之前提到的学习进度和问题，在后续对话中延续话题（如：“上次你提到在学函数，现在对参数传递掌握得怎么样了？”），根据用户的进步调整指导深度。
   6. **心态支持**：编程学习中遇到挫折是常见的，当用户表现出困惑或焦虑时，给予鼓励（如：“这个知识点确实容易混淆，很多初学者都会遇到，我们一步步拆解来看”），增强其学习信心。
   
   ### 沟通原则
   - 语言通俗易懂，避免过多专业术语；若必须使用，需立即解释（例如：“‘面向对象’简单来说就是把问题拆分成一个个‘对象’，每个对象有自己的属性和行为，比如……”）。
   - 多使用提问引导用户思考（如：“你觉得这段代码中，循环的终止条件应该如何设置？”），而非直接给出答案。
   - 结合生活实例类比抽象概念（如：“变量就像一个贴了标签的盒子，你可以往里面放数据，也可以随时取出或更换内容”）。
   - 当用户的问题涉及未学习的知识时，先说明知识关联（如：“这个问题需要用到‘指针’的概念，这是C语言的重点，我们可以先从基础的内存地址开始讲起”），再逐步展开。
   
   请以友好、亲切的语气回应用户，让用户感受到你在认真倾听并针对性地提供帮助，成为其编程学习路上的可靠伙伴。
   ```

   在正式开发前，建议先通过 AI 大模型应用平台对提示词进行测试和调优，观察效果：

   ![1761734826523](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761734826523.png)

2. **多轮对话实现**

   要实现具有 “记忆力” 的 AI 应用，让 AI 能够记住用户之前的对话内容并保持上下文连贯性，我们可以使用 Spring AI 框架的 **对话记忆能力**。

   如何使用对话记忆能力呢？参考 Spring AI 的官方文档，了解到 Spring AI 提供了 ChatClient API 来和大模型进行交互。

   通过示例代码，能够感受到 ChatModel 和 ChatClient 的区别。ChatClient 支持更复杂灵活的链式调用（Fluent API）：

   ```java
   // 基础用法(ChatModel)
   ChatResponse response = chatModel.call(new Prompt("你好"));
   
   // 高级用法(ChatClient)
   ChatClient chatClient = ChatClient.builder(chatModel)
       .defaultSystem("你是恋爱顾问")
       .build();
       
   String response = chatClient.prompt().user("你好").call().content();
   
   ```

   ChatClient 就像如下图所示，包含了选取的大模型以及种种配置。

   ![1761735969350](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761735969350.png)

   Spring AI 提供了多种构建 ChatClient 的方式，比如自动注入、通过建造者模式手动构造：

   这里有个需要注意的点，可以看到第一个构造器注入其实是没有注入相关的 chatModel 的，这是因为我们先前引入了大模型相关的 starter，所以在 spring 启动的时候会默认注入一个 chatModel 进入 ChatClient 中。

   ```java
   // 方式1：使用构造器注入
   @Service
   public class ChatService {
       private final ChatClient chatClient;
       
       public ChatService(ChatClient.Builder builder) {
           this.chatClient = builder
               .defaultSystem("你是恋爱顾问")
               .build();
       }
   }
   
   // 方式2：使用建造者模式
   ChatClient chatClient = ChatClient.builder(chatModel)
       .defaultSystem("你是恋爱顾问")
       .build();
   
   ```

   ChatClient 支持多种响应格式，比如返回 ChatResponse 对象、返回实体对象、流式返回：

   ```java
   // ChatClient支持多种响应格式
   // 1. 返回 ChatResponse 对象（包含元数据如 token 使用量）
   ChatResponse chatResponse = chatClient.prompt()
       .user("Tell me a joke")
       .call()
       .chatResponse();
   
   // 2. 返回实体对象（自动将 AI 输出映射为 Java 对象）
   // 2.1 返回单个实体
   record ActorFilms(String actor, List<String> movies) {}
   ActorFilms actorFilms = chatClient.prompt()
       .user("Generate the filmography for a random actor.")
       .call()
       .entity(ActorFilms.class);
   
   // 2.2 返回泛型集合
   List<ActorFilms> multipleActors = chatClient.prompt()
       .user("Generate filmography for Tom Hanks and Bill Murray.")
       .call()
       .entity(new ParameterizedTypeReference<List<ActorFilms>>() {});
   
   // 3. 流式返回（适用于打字机效果）
   Flux<String> streamResponse = chatClient.prompt()
       .user("Tell me a story")
       .stream()
       .content();
   
   // 也可以流式返回ChatResponse
   Flux<ChatResponse> streamWithMetadata = chatClient.prompt()
       .user("Tell me a story")
       .stream()
       .chatResponse();
   
   ```

   可以给 ChatClient 设置默认参数，比如系统提示词，还可以在对话时动态更改系统提示词的变量，类似模板的概念：

   ```java
   // 定义默认系统提示词
   ChatClient chatClient = ChatClient.builder(chatModel)
           .defaultSystem("You are a friendly chat bot that answers question in the voice of a {voice}")
           .build();
   
   // 对话时动态更改系统提示词的变量
   chatClient.prompt()
           .system(sp -> sp.param("voice", voice))
           .user(message)
           .call()
           .content());
   
   ```

   此外，还支持指定默认对话选项、默认拦截器、默认函数调用等等。

3. **Adivisors**

   Spring AI 使用 Advisors（顾问）机制来增强 AI 的能力，可以理解为一系列可插拔的拦截器，在调用 AI 前和调用 AI 后可以执行一些额外的操作，比如：

   - 前置增强：调用 AI 前改写一下 Prompt 提示词、检查一下提示词是否安全
   - 后置增强：调用 AI 后记录一下日志、处理一下返回的结果

   我们可以直接为 ChatClient 指定默认拦截器，比如对话记忆拦截器 MessageChatMemoryAdvisor 可以帮助我们实现多轮对话能力，省去了自己维护对话列表的麻烦。

   ```java
   var chatClient = ChatClient.builder(chatModel)
       .defaultAdvisors(
           new MessageChatMemoryAdvisor(chatMemory), // 对话记忆 advisor
           new QuestionAnswerAdvisor(vectorStore)    // RAG 检索增强 advisor
       )
       .build();
   
   String response = this.chatClient.prompt()
       // 对话时动态设定拦截器参数，比如指定对话记忆的 id 和长度
       .advisors(advisor -> advisor.param("chat_memory_conversation_id", "678")
               .param("chat_memory_response_size", 100))
       .user(userText)
       .call()
   	.content();
   
   ```

   Advisors 的原理图如下：

   ![顾问 API 流程](https://docs.spring.io/spring-ai/reference/_images/advisors-flow.jpg)

   实际开发中，往往我们会用到多个拦截器，组合在一起相当于一条拦截器链条（责任链模式的设计思想）。每个拦截器是由顺序的，通过 `getOrder()`方法获取到顺序，得到的值越低，越优先执行。所以拦截器的执行顺序不是简单地根据代码的编写顺序决定的。

   Advisors 分为 2 中模式：流式 Streaming 和非流式 Non-Streaming，二者在用法上没有明显区别，返回值不同罢了。但是如果我们要自主实现 Advisors，为了保证通用性，最好还是同时实现流式和非流式的环绕通知方法。

4. Chat Memory Advisor

   前面我们提到了，想要实现对话记忆功能，可以使用 Spring AI 的 ChatMemoryAdvisor，它主要有集中内置的实现方式：

   - MessageChatMemoryAdvisor：从记忆中检索历史对话，并将其作为消息集合添加到提示词中
   - PromptChatMemoryAdvisor：从记忆中检索历史对话，并将其添加到提示词的系统文本中
   - VectorStoreChatMemoryAdvisor：可以用向量数据库来存储检索历史对话

   1. MessageChatMemoryAdvisor 将对话历史作为一系列独立的消息添加到提示中，保留原始对话的完整结构，包括每条消息的角色标识（用户、助手、系统）。

      ```json
      [
        {"role": "user", "content": "你好"},
        {"role": "assistant", "content": "你好！有什么我能帮助你的吗？"},
        {"role": "user", "content": "讲个笑话"}
      ]
      
      ```

   2. PromptChatMemoryAdvisor 将对话历史添加到提示词的系统文本部分，因此可能会失去原始的消息边界。

      ```json
      以下是之前的对话历史：
      用户: 你好
      助手: 你好！有什么我能帮助你的吗？
      用户: 讲个笑话
      
      现在请继续回答用户的问题。
      
      ```

   一般情况下，更建议使用第一种。

5. Chat Memory

   上述 ChatMemoryAdvisor 都依赖 Chat Memory 进行构造，Chat Memory 负责历史对话的存储，定义了保存消息、查询消息、清空消息历史的方法。

   ![1761737285723](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761737285723.png)

   Spring AI 内置了几种 Chat Memory，可以将对话保存到不同的数据源中，比如：

   - InMemoryChatMemory：内存存储
   - CassandraChatMemory：在 Cassandra 中带有过期时间的持久化存储
   - Neo4jChatMemory：在 Neo4j 中没有过期时间限制的持久化存储
   - JdbcChatMemory：在 JDBC 中没有过期时间限制的持久化存储



### 多轮对话 AI 应用开发

1. 首先初始化 ChatClient 对象。使用 Spring 的构造器注入方式注入阿里大模型 dashscopeChatModel 对象，并使用该对象来初始化 ChatClient。初始化指定默认的系统 Prompt 和基于内存的对话记忆 Advisor。代码如下：

   ```java
   public class CodingTeachApp {
   
       private final ChatClient chatClient;
   
       private static final String SYSTEM_PROMPT = "你是一位耐心、专业的编程学习导师，专注于帮助普通人从零开始掌握编程技能。你的核心任务是理解用户的学习目标、当前水平和遇到的具体问题，通过模拟真实的编程学习咨询场景，提供个性化的指导和支持。\n" +
               "\n" +
               "### 核心职责\n" +
               "1. **场景化互动**：以自然对话的方式与用户交流，像真实导师一样询问用户的学习背景（如是否有编程基础、学习编程的目的、目前在学的编程语言等），避免机械性回复。\n" +
               "2. **问题拆解与解答**：当用户提出编程问题时，先判断问题难度是否匹配其水平。若问题过难，先铺垫基础概念；若问题较基础，可结合实例讲解，确保用户理解原理而非仅记住答案。\n" +
               "3. **学习路径引导**：根据用户的目标（如职业发展、兴趣爱好、考试需求等），推荐阶段性的学习内容和资源（如适合的教材、在线课程、练习平台），并提醒学习中的重点和常见误区。\n" +
               "4. **实践指导**：鼓励用户通过代码练习巩固知识，对用户编写的代码提供具体修改建议，指出逻辑错误、优化方向，并解释原因（例如：“这段循环逻辑可以简化，因为……”）。\n" +
               "5. **持续跟进与调整**：记住用户之前提到的学习进度和问题，在后续对话中延续话题（如：“上次你提到在学函数，现在对参数传递掌握得怎么样了？”），根据用户的进步调整指导深度。\n" +
               "6. **心态支持**：编程学习中遇到挫折是常见的，当用户表现出困惑或焦虑时，给予鼓励（如：“这个知识点确实容易混淆，很多初学者都会遇到，我们一步步拆解来看”），增强其学习信心。\n" +
               "\n" +
               "### 沟通原则\n" +
               "- 语言通俗易懂，避免过多专业术语；若必须使用，需立即解释（例如：“‘面向对象’简单来说就是把问题拆分成一个个‘对象’，每个对象有自己的属性和行为，比如……”）。\n" +
               "- 多使用提问引导用户思考（如：“你觉得这段代码中，循环的终止条件应该如何设置？”），而非直接给出答案。\n" +
               "- 结合生活实例类比抽象概念（如：“变量就像一个贴了标签的盒子，你可以往里面放数据，也可以随时取出或更换内容”）。\n" +
               "- 当用户的问题涉及未学习的知识时，先说明知识关联（如：“这个问题需要用到‘指针’的概念，这是C语言的重点，我们可以先从基础的内存地址开始讲起”），再逐步展开。\n" +
               "\n" +
               "请以友好、亲切的语气回应用户，让用户感受到你在认真倾听并针对性地提供帮助，成为其编程学习路上的可靠伙伴。";
   
   
       public CodingTeachApp(ChatModel dashscopeChatModel) {
           ChatMemory chatMemory = new InMemoryChatMemory();
           chatClient = ChatClient.builder(dashscopeChatModel)
                   .defaultSystem(SYSTEM_PROMPT)
                   .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
                   .build();
       }
   }
   ```

2. 编写对话方法。调用 chatClient 对象，传入用户 Prompt，并且给 advisor 指定对话 id 和对话记忆大小。代码如下：

   ```java
   public String doChat(String message,String chatId){
           ChatResponse chatResponse = chatClient
                   .prompt()
                   .user(message)
                   .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                           .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                   .call()
                   .chatResponse();
           String content = chatResponse.getResult().getOutput().getText();
           log.info("content: {}", content);
           return content;
       }
   
   ```

3. 编写单元测试，测试多轮对话：

   ```java
   @SpringBootTest
   class CodingTeachAppTest {
   
       @Resource
       private CodingTeachApp codingTeachApp;
   
       @Test
       void doChat() {
           String chatId = UUID.randomUUID().toString();
           // 第一轮
           String message = "你好，我是橙汁";
           String result = codingTeachApp.doChat(message,chatId);
           // 第二轮
           message = "我想学习java";
           result = codingTeachApp.doChat(message,chatId);
           Assertions.assertNotNull(result);
           // 第三轮
           message = "我想学习什么编程语言？我刚才给你说过，帮我回忆一下";
           result = codingTeachApp.doChat(message,chatId);
           Assertions.assertNotNull(result);
   
       }
   }
   
   ```

   运行结果如图，显然对话记忆生效了

   ![1761738783494](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761738783494.png)

   如果不使用 Spring AI 框架，就要自己维护消息列表，代码将非常复杂！

   ![1761738932030](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761738932030.png)



### 扩展知识

接下来是 Spring AI 的实用特性，包括自定义 Advisor、结构化输出、对话记忆持久化、Prompt 模板和多模态。

#### 自定义 Advisor

虽然 Spring AI 已经内置了 SimpleLoggerAdvisor 日志拦截器，但是以 Debug 级别输入日志，而默认 Spring Boot 项目的日志级别是 info，所以看不到打印的日志消息。

我们可以通过修改配置文件来指定特定文件的输出级别，就能看到打印的日志了：

```yaml
logging:
  level:
    org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor: debug

```

我们可以参考官方文档和内置的 SimpleLoggerAdvisor 源码，结合 2 者略做修改，开发一个更精简的、可自定义级别的日志记录器。默认打印 info 级别日志、并且只输出单词用户提示词和 AI 回复的文本。

代码如下：

具体 getOrder 自己设置，aroundCall 和 aroundStream 可以照抄 SimpleLoggerAdvisor 源码。before 和 observeAfter 直接使用 @Slf4j 的 log.info 输出，并且内容自己调整即可。

```java
package com.chengzhi.chengzhiaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * @author 徐晟智
 * @version 1.0
 */
@Slf4j
public class CodingLoggerAdvisor implements CallAroundAdvisor , StreamAroundAdvisor {
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);
    }

    private AdvisedRequest before(AdvisedRequest request) {
        log.info("AI request: {}", request.userText());
        return request;
    }

    private void observeAfter(AdvisedResponse advisedResponse) {
        log.info("response: {}", advisedResponse.response().getResult().getOutput().getText());
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

```



可以再自定义一个 Re-Reading Advisor，又称为 Re2。该技术通过让模型重新阅读问题来提高推理能力。

注意，虽然该技术可提高大语言模型的推理能力，不过成本会加倍。

Re2 的实现原理很简单，改写用户 Prompt 为下列格式，也就是让 AI 重复阅读用户的输入：

```markdown
{Input_Query}
Read the question again: {Input_Query}
```

需要对请求进行拦截并改写 userText，对应实现代码如下：

```java
package com.chengzhi.chengzhiaiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 Re2 Advisor
 * 可提高大型语言模型的推理能力
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    /**
     * 执行请求前，改写 prompt
     * @param advisedRequest
     * @return
     */
    private AdvisedRequest before(AdvisedRequest advisedRequest) {

        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
        advisedUserParams.put("re2_input_query", advisedRequest.userText());
        

        return AdvisedRequest.from(advisedRequest)
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                .userParams(advisedUserParams)
                .build();
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
```



### 结构化输出 - 编程指导报告开发

结构化输出转换器是 Spring AI 提供的一种实用机制，用于将大语言模型返回的文本输出转换为结构化数据格式，如 JSON、XML 或 Java 类，这对于需要可靠解析 AI 输出值的下游应用程序非常重要。

**基本原理 - 工作流程**

结构化输出转换器在大模型调用前后都发挥作用：

- 调用前：转换器会在提示词后面附加格式指令，明确告诉模型应该生成何种结构的输出，引导模型生成符合指定格式的响应。
- 调用后：转换器将模型的文本输出转换为结构化类型的实例，比如将原始文本映射为 JSON、XML 或特定的数据结构。

![1761741077529](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761741077529.png)

注意，结构化输出转换器只是 **尽最大努力** 将模型输出转换为结构化数据，AI 模型不保证一定按照要求返回结构化输出。有些模型可能无法理解提示词或无法按要求生成结构化输出。建议在程序中实现验证机制或者异常处理机制来确保模型输出符合预期。

**进阶原理 - API 设计**

结构化输出转换器 StructuredOutputConverter 接口允许开发者获取结构化输出，例如将输出映射到 Java 类或值数组。接口定义如下：

```java
public interface StructuredOutputConverter<T> extends Converter<String, T>, FormatProvider {
}

```

它集成了两个关键接口：

- FormatProvider：提供特定的格式指令给 AI 模型
- Spring 的 Converter<String,T> 接口：负责将模型的文本输出转换为指定的目标类型 T

```java
public interface FormatProvider {
    String getFormat();
}

```

![1761741483790](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761741483790.png)

1. 在调用大模型之前，FormatProvider 会为 AI 模型提供特定的格式指令，使其能够生成可以通过 Converter

   转换为指定目标类型的文本输出。

   ```markdown
   Your response should be in JSON format.
   The data structure for the JSON should match this Java class: java.util.HashMap
   Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
   
   ```

   通常，使用 PromptTemplate 将格式指令附加到用户输入的末尾，示例代码如下：

   ```java
   StructuredOutputConverter outputConverter = ...
   String userInputTemplate = """
           ... 用户文本输入 ....
           {format}
           """; // 用户输入，包含一个“format”占位符。
   Prompt prompt = new Prompt(
           new PromptTemplate(
                   this.userInputTemplate,
                   Map.of(..., "format", outputConverter.getFormat()) // 用转换器的格式替换“format”占位符
           ).createMessage());
   
   ```



**编程学习指导报告功能开发**

下面让我们使用结构化输出，来为用户生成编程指导报告，并转换为报告对象，包含报告标题和指导建议字段。

先引入JSON Schema 生成依赖：

```xml
<dependency>
    <groupId>com.github.victools</groupId>
    <artifactId>jsonschema-generator</artifactId>
    <version>4.38.0</version>
</dependency>

```

在 CodingTeachApp 中定义指导报告类，可以使用 Java 14 引入的 record 特性快速定义；

```java
record CodingTeachReport(String title, List<String> suggestions){

}

```

在 CodingTeachApp 中编写一个新的方法，复用之前构造好的 ChatClient 对象，只需额外补充原有的系统提示词、并且添加结构化输出的代码即可。

```java
public CodingTeachReport doChatWithReport(String message,String chatId){
        CodingTeachReport codingTeachReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成编程指导结果，标题为{用户名}的指导报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(CodingTeachReport.class);
        log.info("codingTeachReport: {}", codingTeachReport);
        return codingTeachReport;
    }

```

编写单元测试代码：

```java
@Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是橙汁，我想学习 java，但是我不知道该怎么学习";
        CodingTeachApp.CodingTeachReport codingTeachReport = codingTeachApp.doChatWithReport(message, chatId);
        Assertions.assertNotNull(codingTeachReport);
    }

```

结果如下：

```java
"suggestions": [
    "从Java基础语法开始学习，例如变量、数据类型、运算符和控制流程。",
    "安装JDK并配置开发环境，推荐使用IntelliJ IDEA或Eclipse作为开发工具。",
    "通过在线教程或经典书籍如《Java核心技术》卷I进行系统学习。",
    "动手编写小程序，如计算器或学生信息管理系统，以巩固所学知识。",
    "理解面向对象编程（OOP）概念，包括类、对象、继承、封装和多态。",
    "学习Java常用类库，如集合框架、异常处理和输入输出流。",
    "参与开源项目或在GitHub上阅读他人代码，提升实际编码能力。",
    "定期刷题练习，使用LeetCode或牛客网等平台提高算法与逻辑思维能力。"
  ],
  "title": "橙汁的指导报告"
}

```

本质上是 Advisor 上下文中包含了格式指令：

```json
formatParam -> Your response should be in JSON format.
Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
Do not include markdown code blocks in your response.
Remove the ```json markdown from the output.
Here is the JSON Schema instance your output must adhere to:
​```{
  "$schema" : "https://json-schema.org/draft/2020-12/schema",
  "type" : "‌object",
  "properties" ⁡: {
    "suggestions" : ⁠{
      "type" : "array"⁠,
      "items" : {
    ؜    "type" : "string"
      }
    },
    "‌title" : ⁡{
      "⁠type" : "⁠string"
    }
  },
  "ad‌ditionalP⁡roperties⁠" : false
}

```

### 对话记忆持久化

之前我们使用了基于内存的对话记忆来保存对话上下文，但是服务器一旦重启了，对话记忆就会丢失。有时，我们可能希望将对话记忆持久化，保存到文件、数据库、Redis 或者其它对象存储中，怎么实现呢？

Spring AI 提供了 2 种方式。

1. **利用现有依赖实现**

   官方提供了一些第三方数据库的整合支持，可以将对话保存到不同的数据源种。比如：

   内存存储、Cassandra 中带有过期时间的持久化存储、Neo4j 中没有过期时间限制的持久化存储、在 JDBC 中没有过期时间限制的持久化存储

   这里虽然存在 jdbc 的教程，但是实际上 maven 仓库暂时还不存在这个依赖，所以我们选择自定义实现 ChatMemory 来实现 jdbc 持久化存储。 https://docs.spring.io/spring-ai/reference/api/chat-memory.html#_jdbcchatmemoryrepository 

2. **自定义实现**

   ChatMemory 接口的方法并不多，需要实现对话消息的增、查、删：

   ![1761794188251](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761794188251.png)

   参考 InMemoryChatMemory 的源码，其实就是通过 ConcurrentHashMap 来维护对话消息，key 是对话 id（相当于房间号），value 是该对话 id 对应的消息列表。

   ![1761794260514](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761794260514.png)



开始实现自定义 ChatMemory 实现 JDBC 持久化存储对话记忆。

1. 首先在 pom.xml 中添加下面的依赖。

   ```xml
   <dependency>
       <groupId>com.baomidou</groupId>
       <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
       <version>3.5.12</version>
   </dependency>
   <dependency>
       <groupId>com.mysql</groupId>
       <artifactId>mysql-connector-j</artifactId>
       <version>8.0.31</version>
   </dependency>
   
   ```

2. 接下来，在 mysql 中执行如下语句创建相关数据库的对应表.

   ```mysql
   CREATE DATABASE IF NOT EXISTS `chengchi-ai-agent` DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
   
   USE `chengchi-ai-agent`
   
   CREATE TABLE IF NOT EXISTS `conversation_memory`
   (
   	`id`				BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
   	`conversation_id` 	VARCHAR(64) NOT NULL,
   	`type`				VARCHAR(10) NOT NULL,
   	`memory`			TEXT	    NOT NULL,
   	`create_time`		TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
   	`update_time`		TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   	`is_delete`			TINYINT     DEFAULT 0,
   	PRIMARY KEY(`id`),
   	INDEX idx_conv_prefix (conversation_id(10))
   )ENGINE = INNODB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
   ```

   这里建立了一个关于 conversation_id 的前缀索引，查询更快。

3. 使用 MyBatis X - Generator 插件生成 mapper、xml、service 之类。

4. 创建 DAO 层，方便后续调用

   这里可以把 Service 包下的代码删除

   ```java
   package com.chengzhi.chengzhiaiagent.dao;
   
   /**
    * @author 徐晟智
    * @version 1.0
    */
   
   
   import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
   import com.chengzhi.chengzhiaiagent.mapper.ConversationMemoryMapper;
   import com.chengzhi.chengzhiaiagent.model.domain.ConversationMemory;
   import org.springframework.stereotype.Component;
   
   import java.util.List;
   
   
   @Component
   public class ConversationMemoryDAO extends ServiceImpl<ConversationMemoryMapper, ConversationMemory> {
   
   
       public List<ConversationMemory> getMessages(String conversationId) {
           return this.lambdaQuery()
                   .eq(ConversationMemory::getConversationId, conversationId)
                   .list();
       }
   
       public boolean deleteMemory(String conversationId) {
           return this.lambdaUpdate()
                   .eq(ConversationMemory::getConversationId, conversationId)
                   .remove();
       }
   }
   ```

5. 创建枚举类

   ```java
   @Getter
   public enum MessageTypeEnum {
       /**
        * A {@link Message} of type {@literal user}, having the user role and originating
        * from an end-user or developer.
        * @see UserMessage
        */
       USER("user", UserMessage.class),
   
       /**
        * A {@link Message} of type {@literal assistant} passed in subsequent input
        * {@link Message Messages} as the {@link Message} generated in response to the user.
        * @see AssistantMessage
        */
       ASSISTANT("assistant", AssistantMessage.class),
   
       /**
        * A {@link Message} of type {@literal system} passed as input {@link Message
        * Messages} containing high-level instructions for the conversation, such as behave
        * like a certain character or provide answers in a specific format.
        * @see SystemMessage
        */
       SYSTEM("system", SystemMessage.class),
   
       /**
        * A {@link Message} of type {@literal function} passed as input {@link Message
        * Messages} with function content in a chat application.
        * @see ToolResponseMessage
        */
       TOOL("tool", ToolResponseMessage.class);
   
       private final String value;
   
       private final Class<?> clazz;
   
       MessageTypeEnum(String value, Class<?> clazz) {
           this.value = value;
           this.clazz = clazz;
       }
   
       public static MessageTypeEnum fromValue(String value) {
           for (MessageTypeEnum messageType : MessageTypeEnum.values()) {
               if (messageType.getValue().equals(value)) {
                   return messageType;
               }
           }
           throw new IllegalArgumentException("Invalid MessageType value: " + value);
       }
   }
   
   ```

6. 修改配置文件

   ```yaml
     datasource:
       driver-class-name: com.mysql.cj.jdbc.Driver
       url: jdbc:mysql://localhost:3306/chengchi-ai-agent
       username: root
       password: hsp
   
   ```

7. 创建对应的 ChatMemory

   ```java
   @Component
   public class MySQLChatMemory implements ChatMemory {
   
       private final ConversationMemoryDAO conversationMemoryDAO;
   
       public MySQLChatMemory(ConversationMemoryDAO conversationMemoryDAO) {
           this.conversationMemoryDAO = conversationMemoryDAO;
       }
   
   
       @Override
       public void add(String conversationId, List<Message> messages) {
           Gson gson = new Gson();
           List<ConversationMemory> memoryList = messages.stream().map(
                   message -> {
                       String messageType = message.getMessageType().getValue();
                       String mes = gson.toJson(message);
                       return ConversationMemory.builder().conversationId(conversationId)
                               .type(messageType).memory(mes).build();
                   }
           ).toList();
           conversationMemoryDAO.saveBatch(memoryList);
       }
   
       @Override
       public List<Message> get(String conversationId, int lastN) {
           List<ConversationMemory> messages = conversationMemoryDAO.getMessages(conversationId);
           if(CollectionUtil.isEmpty(messages)){
               return List.of();
           }
           return messages.stream()
                   .skip(Math.max(0, messages.size() - lastN))
                   .map(this::getMessage)
                   .collect(Collectors.toList());
       }
   
       @Override
       public void clear(String conversationId) {
           conversationMemoryDAO.deleteMemory(conversationId);
       }
       private Message getMessage(ConversationMemory conversationMemory) {
           String memory = conversationMemory.getMemory();
           Gson gson = new Gson();
           return (Message) gson.fromJson(memory, MessageTypeEnum.fromValue(conversationMemory.getType()).getClazz());
       }
   }
   
   ```

8. 然后修改app中的注入

   ```java
   public CodingTeachApp(ChatModel dashscopeChatModel, MySQLChatMemory chatMemory) {
           //ChatMemory chatMemory = new InMemoryChatMemory();
           chatClient = ChatClient.builder(dashscopeChatModel)
                   .defaultSystem(SYSTEM_PROMPT)
                   .defaultAdvisors(
                           new MessageChatMemoryAdvisor(chatMemory),
                           new CodingLoggerAdvisor())
                   .build();
       }
   
   ```

9. 最后进行测试

   ```java
       @Test
       void doChat() {
           String chatId = UUID.randomUUID().toString();
           // 第一轮
           String message = "你好，我是橙汁";
           String result = codingTeachApp.doChat(message,chatId);
           // 第二轮
           message = "我想学习java";
           result = codingTeachApp.doChat(message,chatId);
           Assertions.assertNotNull(result);
           // 第三轮
           message = "我想学习什么编程语言？我刚才给你说过，帮我回忆一下";
           result = codingTeachApp.doChat(message,chatId);
           Assertions.assertNotNull(result);
   
       }
   
   ```

   ![1761803887643](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761803887643.png)

   可以看到历史对话已经被全部持久化保存到数据库中，成功





### PromptTemplate 模板

PromptTemplate 是 Spirng AI 框架中用于构建和管理提示词的核心组件。允许开发者创建带有占位符的文本模板，然后在运行时动态替换这些占位符。

```java
// 定义带有变量的模板
String template = "你好，{name}。今天是{day}，天气{weather}。";

// 创建模板对象
PromptTemplate promptTemplate = new PromptTemplate(template);

// 准备变量映射
Map<String, Object> variables = new HashMap<>();
variables.put("name", "鱼皮");
variables.put("day", "星期一");
variables.put("weather", "晴朗");

// 生成最终提示文本
String prompt = promptTemplate.render(variables);
// 结果: "你好，鱼皮。今天是星期一，天气晴朗。"

```

```java
String userText‌ = """
    Tell me about three⁡ famous pirates from the Golde⁠n Age of Piracy and why they d⁠id.
    Write at least a sente؜nce for each pirate.
    """;

Message userMessage = new UserMessage(userText);

String sy‌stemText = """
  ⁡You are a helpful⁠ AI assistant tha⁠t helps people fi؜nd information.
  Your name is {name}
  You should reply to the user's request with your name and also in the style of a {voice}.
  """;

SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemText);
Message systemMessage = systemPromptTemplate.createMessage(Map.of("name", name, "voice", voice));

Prompt prompt = new Prompt(List.of(userMessage, systemMessage));

List<Generation> response = chatModel.call(prompt).getResults();

```









## 3、RAG  知识库基础

### 编程学习助手应用的潜在需求

对于我们的 AI 编程学习助手，同样可以利用 AI 知识问答满足很多需求。如果自己想不到需求的化，我们可以利用 AI 来挖掘一些需求，得到的结果如下：

```markdown
以下是 AI 编程学习助手应用的潜在需求，涵盖功能、性能、用户体验、安全等多个维度，可根据目标用户（如入门学习者、进阶开发者等）和场景进一步细化：


### **一、核心功能需求**
1. **代码学习与讲解**
   - 支持主流编程语言（Python、Java、C++、JavaScript 等）的语法、特性、最佳实践讲解，结合示例代码可视化演示。
   - 针对特定知识点（如函数、面向对象、算法等）生成阶梯式学习路径，从基础到进阶逐步引导。
   - 解析用户提供的代码片段，自动生成注释、逻辑流程图或执行步骤说明。

2. **实时编程辅助**
   - 代码补全：根据上下文智能推荐函数、变量名、语法结构，支持自定义补全规则。
   - 错误诊断：实时检测语法错误、逻辑漏洞（如空指针、数组越界），提供修复建议和原因解释。
   - 代码优化：分析代码性能瓶颈（如时间复杂度）、冗余逻辑，推荐更简洁/高效的实现方式。

3. **交互式练习与反馈**
   - 提供分级编程习题（从入门到面试难度），支持自动判题，即时反馈错误位置和改进方向。
   - 支持自定义练习场景（如“实现一个链表反转”“修复内存泄漏”），生成对应测试用例。
   - 模拟面试环境，提供算法题限时训练，附带解题思路提示（非直接答案）。

4. **项目实战指导**
   - 提供小型项目案例（如简易爬虫、待办事项 app），分步骤拆解实现过程，标注关键技术点。
   - 分析用户的项目代码结构，指出模块设计问题（如耦合过高），推荐设计模式（如单例、工厂模式）。
   - 集成版本控制基础教学（如 Git 操作），指导用户如何提交代码、解决冲突。

5. **个性化学习适配**
   - 根据用户学习记录（如常错知识点、练习耗时）生成学习报告，推荐薄弱环节的强化内容。
   - 支持调整讲解风格（如“简洁模式”“详细模式”），适配不同学习节奏（如快节奏刷题、慢节奏原理学习）。
   - 记忆用户的学习偏好（如常用语言、擅长领域），优先推送相关内容。


### **二、用户体验需求**
1. **交互便捷性**
   - 提供代码编辑器（支持语法高亮、自动缩进、快捷键），无需切换外部工具即可编写/运行代码。
   - 支持自然语言提问（如“为什么这个循环会超时？”），AI 需理解问题并转化为技术解答。
   - 移动端适配：简化界面，支持代码片段拍照识别、语音提问（如“解释这段代码的作用”）。

2. **可视化与趣味性**
   - 用动画/图表展示抽象概念（如递归调用栈、多线程执行流程）。
   - 加入成就系统（如“首次独立解决排序算法题”“连续 7 天学习”），提升用户粘性。
   - 支持代码执行过程可视化（如变量值变化、函数调用顺序），帮助理解运行逻辑。

3. **社区与协作（可选）**
   - 允许用户分享自己的解题思路或项目代码，AI 可对分享内容进行点评。
   - 提供同伴学习功能（如组队刷题、代码互评），AI 辅助仲裁争议点。


### **三、性能与技术需求**
1. **响应速度**
   - 代码分析、错误诊断、补全建议的响应时间≤1秒，避免打断编程思路。
   - 代码运行沙箱启动时间≤3秒（支持主流语言的代码在线运行，隔离用户代码与系统环境）。

2. **准确性与可靠性**
   - 语法解析准确率≥99%，逻辑错误识别覆盖率≥90%（针对常见场景）。
   - 代码运行沙箱需严格隔离，防止恶意代码（如无限循环、文件操作）影响系统安全。
   - 定期更新编程语言版本（如 Python 3.12、Java 21）和技术栈（如框架新特性）。

3. **可扩展性**
   - 支持通过插件扩展语言库（如新增 Rust、Go 等小众语言支持）。
   - 预留 API 接口，可与外部工具集成（如 IDE 插件、学习管理系统 LMS）。


### **四、安全与合规需求**
1. **数据安全**
   - 加密存储用户代码和学习记录，支持手动删除数据。
   - 对用户输入的代码进行安全扫描，禁止包含恶意指令（如系统命令注入）。

2. **内容合规**
   - 过滤涉及政治敏感、暴力、侵权的代码或内容。
   - 明确区分 AI 生成内容与用户原创，避免版权纠纷（如注明代码示例的来源）。


### **五、辅助功能需求**
1. **离线模式**
   - 支持下载基础语法库和习题集，在无网络环境下进行学习。

2. **多语言支持**
   - 讲解内容支持中英文切换，适配不同地区用户。

3. **无障碍设计**
   - 支持屏幕阅读器，代码和文字内容可被正确识别朗读。


这些需求可根据目标用户群体（如青少年编程入门、大学生算法训练、职场开发者技能提升）的优先级进行取舍，核心是平衡“教学专业性”“交互便捷性”和“学习趣味性”。
```

### 本项目的具体需求

其实上述需求的实现方式几乎是一致的，所以我们将挑选其中一个实现，重点实现**不同用户群体定制化学习指导**功能，让 AI 编程学习助手不仅能回答用户的编程问题，还能推荐自己出品的相关课程和服务，帮用户解决编程疑惑的同时，实现一波变现。

### 如何让 AI 获取知识？

在实现这个需求前，我们需要思考一个关键问题：**编程知识从哪里获取呢？**

首先 AI 原本就拥有一些通用的知识，对于不会的知识，还可以利用互联网搜索。但是这些都是从网络获取的、公开的知识。对于企业来说，数据是命脉，也是自己独特的价值，随着业务的发展，企业肯定会积累一波自己的知识库，比如往期用户的咨询和成功案例、以及自家的编程指导教程，如果让 AI 能够利用这些知识库进行问答，效果可能会更好，而且更加个性化。

如果不给 AI 提供特定领域的知识库，AI 可能会面临这些问题：

1. 知识有限：AI 不知道你的最新课程和内容
2. 编故事：当 AI 不知道答案时，它可能会 “自圆其说” 编造内容
3. 无法个性化：不了解你的特色服务和回答风格
4. 不会推销：不知道该在什么时候推荐你的付费课程和服务

那么如何让 AI 利用自己的知识库进行问答呢？这就需要用到 AI 主流的技术 ——RAG。



### RAG 概念

#### 什么是 RAG？

RAG（Retrieval-Augmented Generation，检索增强生成）是一种结合信息检索技术和 AI 内容生成的混合架构，可以解决大模型的知识时效性限制和幻觉问题。

简单来说，RAG 就像给 AI 配了一个小本本，上面记录了一些特定领域的相关知识，AI 在回答问题前先查一查特定的知识库来获取知识，确保回答是基于真实资料而不是凭空想象。

从技术角度来看，RAG 在大模型生成回答之前，会先从外部知识库中检索相关信息，然后将这些检索到的内容作为额外上下文提供给模型，引导其生成更准确、更相关的回答。

可以简单了解下 RAG 和传统 AI 模型的区别：

![1761807405760](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761807405760.png)

#### RAG 工作流程

RAG 技术实现主要包含以下 4 个核心步骤，让我们分步来学习：

- 文档收集和切割
- 向量转换和存储
- 文档过滤和检索
- 查询增强和关联

1. **文档收集和切割**

   文档收集：从各种来源（网页、PDF、数据库等）收集原始文档

   文档预处理：清洗、标准化文本格式

   文档切割：将长文档分割成适当大小的片段（俗称 chunks）

   - 基于固定大小（如 512 个 token）
   - 基于语义边界（如段落、章节）
   - 基于递归分割策略（如递归字符 n-gram 切割）

   ![1761807896391](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761807896391.png)

2. **向量转换和存储**

   向量转换：使用 Embedding 模型将文本快转换为高维向量表示，可以捕获到文本的语义特征

   向量存储：将生成的向量和对应文本存入向量数据库，支持高效的相似性搜索

   ![1761808080573](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761808080573.png)

3. **文档过滤和检索**

   查询处理：将用户问题也转换为向量表示

   过滤机制：基于元数据、关键词或自定义规则进行过滤

   相似度搜索：在向量数据库中查找与问题向量最相似的文档块，常用的相似度搜索度算法有余弦相似度、欧式距离等

   上下文组装：将检索到的多个文档块组装成连贯上下文

   这里的 Rank 模型可能是粗略选出其中相关度较高的文档，然后再经过精排，就是更精确的分析和排序，选出topK 也就是最相关的几个文档切片然后返回。

   ![1761808208917](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761808208917.png)

4. **查询增强和关联**

   提示词组装：将检索到的相关文档与用户问题组合成增强提示

   上下文融合：大模型基于增强提示生成回答

   源引用：在回答中添加信息来源引用

   后处理：格式化、摘要或其他处理以优化最终输出

**完整工作流程**

![1761808425128](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761808425128.png)

#### RAG 相关技术

1. **Embedding 和 Embedding 模型**

   Embedding 嵌入是将高纬度离散数据（如文字、图片）转换为低维连续向量的过程。这些向量能在数学空间中表示原始数据的语义特征，使计算机能够理解数据间的相似性。

   Embedding 模型是执行这种转换算法的机器学习莫i选哪个，如 Word2Vec文本、ResNet图像等。不同的模型产生的向量表示和维度数不同，一般维度越高表达能力越强，可以捕获更丰富的语义信息和更细微的差别，但同样占用更多存储空间。

   举个例子，“橙汁” 和 “橙子” 的 Embedding 向量在空间中较接近，而 “ 橙汁” 和 “烤肉” 则相距较远，反映了语义关系。

2. **向量数据库**

   向量数据库是专门存储和检索向量数据的数据库系统。通过高效索引算法实现快速相似性搜索，支持 K 近邻查询等操作。

   ![1761809000766](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761809000766.png)

   注意，并不是只有向量数据库才能存储向量数据，只不过与传统数据库不同，向量数据库优化了高维向量的存储和检索。

   AI 的流行带火了一波向量数据库和向量存储，比如 Milvus、Pinecone 等。此外，一些传统数据库也可以通过安装插件实现向量存储和检索，比如 PGVector、Redis Stack 的 RediSearch 等。

3. **召回**

   召回是信息检索中的第一阶段，目标是从大规模数据集中快速筛选出可能相关的候选项子集。**强调速度和广度，而非精确度。**

   这一阶段是为后续粗滤排序和精细排序提供候选集。

4. **精排和 Rank 模型**

   精排（精确排序）是搜索 / 推荐系统的最后阶段，使用计算复杂度更高的算法，考虑更多特征和业务规则，对少量候选项进行更复杂、精细的排序。

   ![1761809795447](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761809795447.png)

5. **混合检索策略**

   混合检索策略结合多种检索方法的优势，提高检索效果。常见组合包括关键词检索、语义检索、知识图谱等。比如 “基于全文检索的关键词搜索” + “基于向量的语义搜索” 的混合检索测率，用户可以自己设置不同检索方式的权重。



### RAG 实战：Spring AI + 本地知识库

我们实现简化后的 RAG 开发步骤：

1. 文档准备
2. 文档读取
3. 向量转换和存储
4. 查询增强

#### 文档准备

首先准备用于给 AI 知识库提供知识的文档，推荐 Markdown 格式，尽量结构化。

在学习 RAG 的过程中，可以利用 AI 来生成文档，提供一段示例 Prompt：

```markdown
帮我生成 3 篇 Markdown 文章，主题是【编程学习常见问题和回答】，3 篇文章的问题分别针对青少年编程入门、大学生算法训练、职场开发者技能提升的状态，内容形式为 1 问 1 答，每个问题标题使用 4 级标题，每篇内容需要有至少 5 个问题，要求每个问题中推荐一个相关的课程，课程链接都是 https://www.codefather.cn

```

#### 文档读取

首先，我们要对自己准备好的知识库文档进行处理，然后保存到向量数据库中。这个过程俗称 ETL（抽取、转换、加载），Spring AI 提供了对 ETL 的支持，考虑 https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html#_markdown

ETL 的 3 大核心组件，按照顺序执行：

- DocumentReader：读取文档，得到文档列表
- DocumentTransformer：转换文档，得到处理后的文档列表
- DocumentWriter：将文档保存到存储中（可以是向量数据库，也可以是其它存储）

![1761810492956](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761810492956.png)

1. **引入依赖**

   Spring 提供了很多种 DocumentReaders，用于加载不同类型的文件。

   我们使用 MarkdownDocumentReader 来读取 Markdown 文档。依赖如下：

   ```xml
   <dependency>
       <groupId>org.springframework.ai</groupId>
       <artifactId>spring-ai-markdown-document-reader</artifactId>
       <version>1.0.0-M6</version>
   </dependency>
   
   ```

2. 编写文档加载类，负责读取所有 Markdown 文档并转换为 Document 列表。代码如下：

   ```java
   @Component
   @Slf4j
   public class CodingTeachAppDocumentReader {
   
       private final ResourcePatternResolver resourcePatternResolver;
   
       CodingTeachAppDocumentReader(ResourcePatternResolver resourcePatternResolver) {
           this.resourcePatternResolver = resourcePatternResolver;
       }
       public List<Document> loadMarkdowns(){
           List<Document> allDocuments = new ArrayList<Document>();
           try{
               // 这里修改为你要加载的多个 Markdown 文件的路径格式
               Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
               for (Resource resource : resources) {
                   String filename = resource.getFilename();
                   MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                           .withHorizontalRuleCreateDocument(true)
                           .withIncludeBlockquote(false)
                           .withIncludeCodeBlock(false)
                           .withAdditionalMetadata("filename", filename)
                           .build();
                   MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                   allDocuments.addAll(markdownDocumentReader.get());
               }
           }catch (IOException e){
               log.error("Markdown 文档加载失败",e);
           }
           return allDocuments;
       }
   }
   
   ```

   上述代码中，我们通过 MarkdownDocumentReaderConfig 文档加载配置来指定读取文档的细节，比如 md 格式中每个横线作为一个新的 document 对象、是否读取代码块、引用快等。特别需要注意的是，我们还指定了额外的元信息配置，提取文档的文件名(fileName) 作为文档的元信息，可以便于后续知识库实现更精确的检索。

3. **向量转换和存储**

   为了实现方便，我们先使用 Spring AI 内置的、基于内存读写的向量数据库 SimpleVectorStore 来保存文档。

   SimpleVectorStore 实现了 VectorStore 接口，而 VectorStore  接口集成了 DocumentWriter，所以具备文档写入能力。

   简单了解下源码，在将文档写入到数据库前，会先调用 Embedding 大模型将文档转换为向量，实际保存到数据库中的是向量类型的数据。

   以下是代码：

   ```java
   @Configuration
   public class CodingTeachAppVectorConfig {
   
       @Resource
       private  CodingTeachAppDocumentReader codingTeachAppDocumentReader;
   
       @Bean
       VectorStore codingTeachAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
           SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                   .build();
           // 加载文档
           List<Document> documents = codingTeachAppDocumentReader.loadMarkdowns();
           simpleVectorStore.add(documents);
           return simpleVectorStore;
       }
   
   }
   ```

4. **查询增强**

   Spring AI 通过 Advisor 特性提供了开箱即用的 RAG 功能。主要是 QuestionAnswerAdvisor 问答拦截器和 RetrievalAugmentationAdvisor 检索增强拦截器。

   其实原理很简单。向量数据库存储着 AI 模型本身不知道的数据，当用户问题发送给 AI 模型时，QuestionAnswerAdvisor 会查询向量数据库，获取与用户问题相关的文档。然后从向量数据库返回的响应会被附加到用户文本中，为 AI 模型提供上下文，帮助其生成回答。

   1. 先引入依赖，不引入也可正常使用

      ```xml
      <dependency>
         <groupId>org.springframework.ai</groupId>
         <artifactId>spring-ai-advisors-vector-store</artifactId>
      </dependency>
      
      ```

   2. 这里选用 QuestionAnswerAdvisor 问答拦截器。代码如下：

      ```java
      @Resource
          private VectorStore codingTeachAppVectorStore;
      
          public String doChatWithRag(String message,String chatId){
              ChatResponse chatResponse = chatClient
                      .prompt()
                      .user(message)
                      .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                              .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                      .advisors(new CodingLoggerAdvisor())
                      .advisors(new QuestionAnswerAdvisor(codingTeachAppVectorStore))
                      .call()
                      .chatResponse();
              String content = chatResponse.getResult().getOutput().getText();
              log.info("content: {}", content);
              return content;
          }
      
      ```

   3. 进行测试

      ```java
      @Test
          void doChatWithRag() {
              String chatId = UUID.randomUUID().toString();
              String message = "你好，我是橙汁，我现在是一名大学生，想学习编程，但是我不知道该怎么学习";
              String result = codingTeachApp.doChatWithRag(message, chatId);
              Assertions.assertNotNull(result);
          }
      
      ```

      结果如下：

      里面是用到了我 md 中的内容，测试成功。

      ```java
      根据你的情况，我建议你可以从 **Python** 这门语言入手。它语法简单、易读易懂，特别适合初学者。比如你可以用几行代码就实现一个计算器、画出有趣的图形，甚至做出小游戏，很快就能看到成果，很有成就感。
      
      ### 你可以这样开始：
      1. **选择一门适合入门的课程**：推荐你试试《青少年编程入门到精通》或类似的 Python 入门课，虽然是为青少年设计的，但内容清晰有趣，非常适合零基础的同学。
      2. **边学边动手**：不要只看视频或看书，一定要自己动手写代码。可以尝试做一个简单的项目，比如“成绩统计小工具”或者“待办事项清单”。
      3. **设定小目标**：比如第一周学会变量和条件判断，第二周做一个猜数字小游戏。每完成一个小目标，都会让你更有信心。
      4. **加入学习社群**：和其他同学一起学习、交流问题、分享作品，不仅能坚持得更久，还能学到更多。
      
      如果你喜欢挑战，之后还可以参加一些编程竞赛，或者报名《编程实践项目班》，在真实项目中提升能力。
      
      记住，编程不是一蹴而就的事，进步慢也没关系，关键是持续行动。只要你愿意坚持，一定能学好！
      
      如果需要，我可以一步步陪你规划学习路径哦 😊
      
      ```



### RAG 实战：Spring AI + 云知识库服务

上面，我们的文档读取、文档加载、向量数据库是在本地通过编程的方式实现的。其实还有另一种模式，直接使用别人提供的云知识库来简化 RAG 的开发。

这里我们选择 阿里云百炼，因为 Spring AI Alibaba 可以和它轻松集成，简化 RAG 开发。

1. 准备数据，在应用开发里面，选取知识库，进行创建知识库。

   ![1761814186035](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761814186035.png)

   ![1761814371248](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761814371248.png)

2. RAG 开发，代码如下。

   ```java
   @Configuration
   @Slf4j
   public class CodingTeachAppRagCloudAdvisorConfig {
   
       @Value("${spring.ai.dashscope.api-key}")
       private String dashscopeApiKey;
   
       @Bean
       public Advisor codingTeachAppRagCloudAdvisor() {
           DashScopeApi dashScopeApi = new DashScopeApi(dashscopeApiKey);
           final String KNOWLEDGE_INDEX = "编程学习助手";
           DashScopeDocumentRetriever documentRetriever = new DashScopeDocumentRetriever(dashScopeApi, DashScopeDocumentRetrieverOptions.builder()
                   .withIndexName(KNOWLEDGE_INDEX)
                   .build());
           return RetrievalAugmentationAdvisor.builder()
                   .documentRetriever(documentRetriever)
                   .build();
       }
   }
   
   ```

   ```java
   public String doChatWithRag(String message,String chatId){
           ChatResponse chatResponse = chatClient
                   .prompt()
                   .user(message)
                   .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                           .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                   .advisors(new CodingLoggerAdvisor())
                   // 本地知识库 rag
                   //.advisors(new QuestionAnswerAdvisor(codingTeachAppVectorStore))
                   // 云上知识库 rag
                   .advisors(codingTeachAppRagCloudAdvisor)
                   .call()
                   .chatResponse();
           String content = chatResponse.getResult().getOutput().getText();
           log.info("content: {}", content);
           return content;
       }
   ```

   进行测试，结果如下：

   ```java
   content: 你好，橙汁！作为大学生想学习编程是个非常棒的选择。你可以从一门适合初学者的编程语言开始，比如 Python。它语法简单、易读易懂，非常适合入门。
   
   建议你按照以下步骤来学习：
   
   1. **打好基础**：先学习变量、条件语句、循环、函数等基本概念。可以通过一些互动式课程或视频系统学习。
   2. **多动手写代码**：光看不练很难掌握。尝试自己写小程序，比如计算器、猜数字游戏等，边学边练。
   3. **拆解问题**：遇到复杂题目时，学会把它分解成小问题，一个个解决。这能帮助你培养编程思维。
   4. **做项目实践**：当你掌握基础知识后，可以尝试做个小项目，比如个人博客、待办事项应用，这样能巩固所学知识。
   5. **参加刷题训练**：如果你想提升算法能力，可以从简单的算法题开始，每天坚持练习，逐步提高。
   6. **加入学习社群**：和志同道合的同学一起学习，互相鼓励、分享成果，会让你更有动力。
   
   推荐课程：[青少年编程入门到精通](https://www.codefather.cn/) 和 [编程语法实战训练营](https://www.codefather.cn/)，虽然是为青少年设计的，但内容通俗易懂，对编程新手非常友好。还有 [算法实战刷题班](https://www.codefather.cn/) 帮助你提升解题能力。
   
   坚持下去，你会看到自己的进步！
   ```

   



## 4、RAG 知识库进阶

1. **文档**

   Document：不仅仅可以存储文本内容，里面的 Media 可以用来存储音频、视频等额外的信息。还有一个 MetaData 元信息，这是一个 map 类型的，起到一个给文档加附加信息的作用，方便你去做筛选和标注。

   ETL 三大步骤（抽取、转换、存储）：主要就是依靠 DocumentReader、DocumentTransformer、DocumentWriter 实现的。

   DocumentReader 的源码如下：

   ```java
   public interface DocumentReader extends Supplier<List<Document>> {
   
   	default List<Document> read() {
   		return get();
   	}
   
   }
   
   ```

   如果你想自己实现一个用来读取任何文件的 DocumentReader 的话，本质上其实就是从任何文件中读取到数据之后，再把它转换为一个 Document 对象，text 存放实际文本内容，media 存放音频或者视频等媒体内容，然后 metadata 存放一些额外的标记信息即可。

   DocumentTransformer 的源码如下：

   ```java
   public interface DocumentTransformer extends Function<List<Document>, List<Document>> {
   
   	default List<Document> transform(List<Document> transform) {
   		return apply(transform);
   	}
   
   }
   
   ```

   它传入一个 Document 的列表，然后再返回一个 Document 的列表，所以重要的其实是中间进行转换的代码，也就是这个 apply，主要做的是分割和转换的操作。

   比如一个经典的 TokenTextSplitter ，它的构造方式如下，其实具体的就是每个文本块的大小，每个文本块的最小大小，以及要包含最少几个块，最多几个块，是否在块中保留分隔符。

   ```java
   public TokenTextSplitter(int chunkSize, int minChunkSizeChars, int minChunkLengthToEmbed, int maxNumChunks,
   			boolean keepSeparator)
   
   ```

   还有一个关键的 MetadataEnricher 元数据增强器，它的作用是给文档补充更多的元信息，经典的有两个：

   KeywordMetadataEnricher（使用 AI 提取关键词加到文档的元信息中） 和 SummaryMetadataEnricher（使用 AI 总结文档摘要然后添加到元数据中）

   还有最后一个 ContentFormatter ，它的作用就是给文档中的元数据的格式或内容经过一个筛选或改变，得到一批新的元数据然后覆盖掉原本的元数据。

   最后是 DocumentWriter，源码如下：

   ```java
   public interface DocumentWriter extends Consumer<List<Document>> {
   
   	default void write(List<Document> documents) {
   		accept(documents);
   	}
   
   }
   
   ```

   主要就是把处理后的 document 写到目标存储中，目前有两种实现，一种是 FileDocumentWriter，将文档写入到文件系统中；另一种就是 VectorStoreWriter ，将文档写入到向量数据库中。

2. **向量存储和转换**

   VectorStore 是与向量数据库交互的核心接口，它继承自 DocumentWriter，主要提供如下功能：

   简单来说就是：添加文档到向量库、从向量库中删除文档、基于查询进行相似度搜索、获取原生客户端

   ```java
   public interface VectorStore extends DocumentWriter {
   
       default String getName() {
           return this.getClass().getSimpleName();
       }
   
       void add(List<Document> documents);
   
       void delete(List<String> idList);
   
       void delete(Filter.Expression filterExpression);
   
       default void delete(String filterExpression) { ... };
   
       List<Document> similaritySearch(String query);
   
       List<Document> similaritySearch(SearchRequest request);
   
       default <T> Optional<T> getNativeClient() {
           return Optional.empty();
       }
   }
   
   ```

   其中的 similaritySearch 用于进行相似度查询，里面的查询参数 SearchRequest 用来构造一个复杂的查询请求。

   ```java
   SearchRequest request = SearchRequest.builder()
       .query("什么是程序员鱼皮的编程导航学习网 codefather.cn？")
       .topK(5)                  // 返回最相似的5个结果
       .similarityThreshold(0.7) // 相似度阈值，0.0-1.0之间
       .filterExpression("category == 'web' AND date > '2025-05-03'")  // 过滤表达式
       .build();
   
   List<Document> results = vectorStore.similaritySearch(request);
   
   ```

   



### 基于 PGVector 实现向量存储

自己先在阿里云上开通相关的云上数据库，然后使用 PG数据库，安装相应的插件之后。

在 yaml 中添加相关的数据源的设置，地址、用户名以及密码，测试可以联通后。

如下为代码：

```java
@Configuration
public class PgVectorVectorStoreConfig {

    @Resource
    private CodingTeachAppDocumentReader codingTeachAppDocumentReader;

    @Bean
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // Optional: defaults to model dimensions or 1536
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE
                .indexType(HNSW)                     // Optional: defaults to HNSW
                .initializeSchema(true)              // Optional: defaults to false
                .schemaName("public")                // Optional: defaults to "public"
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000
                .build();
        return vectorStore;
    }

}

```

然后将其作为 bean 注入，在 app 中通过 QuestionAnswerAdvisor 添加

这里为了调试和部署方便，还是使用的云上知识库 rag，但是基于远程 pg 数据库的可以跑通

```java
public String doChatWithRag(String message,String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new CodingLoggerAdvisor())
                // 本地知识库 rag
                //.advisors(new QuestionAnswerAdvisor(codingTeachAppVectorStore))
                // 云上知识库 rag
                .advisors(codingTeachAppRagCloudAdvisor)
                // rag 基于pg远程向量数据库
                //.advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

```



### RAG 最佳实践和调优

#### 文档收集和切割

文档的质量决定了 AI 回答能力的上限，其它优化策略只是让 AI 回答能力不断接近上限。

所以文档处理是 RAG 系统中最基础也最重要的环节。

1. **优化原始文档**

   - 内容结构化：
     - 原始文档的排版应该清晰、结构合理。
     - 文档的各级标题层次分明，内容表达清晰
     - 列表中间的某一条之下尽量不要再次分级，减少层次嵌套
   - 内容规范化：
     - 语言统一：文档语言类型和用户提示词一致，比如英文文档和英文提示词
     - 表述统一：相同含义的词语统一格式，比如 ML，Machine Learning 都规范为 “机器学习”
     - 减少噪音：尽量避免水印、表格和图片等可能影响的元素
   - 格式标准化：
     - 优先使用 Markdown、DOC\DOCX 等文本格式
     - 如果文档包含图片等，尽量使用可以直接访问的公网链接

2. **文档切片**

   合适的文档切片大小和切片方式也至关重要。

   过短的切片可能导致语义缺失，切片过长可能会引入无关信息；同时也要考虑到用户提示词的复杂程度（复杂的话就把切片稍微增长）

   最佳策略是可以结合**智能分块算法和人工二次校验**。（在大模型平台上最好使用自带的**智能切分**功能，不要使用 Spring AI 提供的 DocumentTransformer 提供的切分文档功能，不太完善）

   如下所示即 Spring AI 提供的分块算法，效果不是很好：

   ```java
   @Component
   class MyTokenTextSplitter {
       public List<Document> splitDocuments(List<Document> documents) {
           TokenTextSplitter splitter = new TokenTextSplitter();
           return splitter.apply(documents);
       }
   
       public List<Document> splitCustomized(List<Document> documents) {
           TokenTextSplitter splitter = new TokenTextSplitter(200, 100, 10, 5000, true);
           return splitter.apply(documents);
       }
   }
   ```

   使用切分器：

   ```java
   @Resource
   private MyTokenTextSplitter myTokenTextSplitter;
   
   @Bean
   VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
       SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
               .build();
       // 加载文档
       List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
       // 自主切分
       List<Document> splitDocuments = myTokenTextSplitter.splitCustomized(documents);
       simpleVectorStore.add(splitDocuments);
       return simpleVectorStore;
   }
   ```

   一般使用智能切分策略时，知识库会：

   1. 首先利用分局标识符将文档划分为若干段落
   2. 其实基于划分的段落，使用语义相关性自适应选择切片点进行划分。

   并且切分后可以进入知识库选择切片进行查看，不合适了再进行人工调整。

3. **元数据标注**

   可以为切分后的文档添加丰富的结构化信息，俗称元信息，便于后续向量化处理和精准检索。

   1. 手动添加元信息（单个文档）

      ```java
      documents.add(new Document(
          "案例编号：LR-2023-001\n" +
          "项目概述：180平米大平层现代简约风格客厅改造\n" +
          "设计要点：\n" +
          "1. 采用5.2米挑高的落地窗，最大化自然采光\n" +
          "2. 主色调：云雾白(哑光，NCS S0500-N)配合莫兰迪灰\n" +
          "3. 家具选择：意大利B&B品牌真皮沙发，北欧白橡木茶几\n" +
          "空间效果：通透大气，适合商务接待和家庭日常起居",
          Map.of(
              "type", "interior",    // 文档类型
              "year", "2025",        // 年份
              "month", "05",         // 月份
              "style", "modern",      // 装修风格
          )));
      
      ```

   2. 利用 DocumentReader 批量添加元信息

      我们可以在 loadMarkdown 时为每篇文档添加特定标签，

      ```java
      public List<Document> loadMarkdowns(){
              List<Document> allDocuments = new ArrayList<Document>();
              try{
                  // 这里修改为你要加载的多个 Markdown 文件的路径格式
                  Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
                  for (Resource resource : resources) {
                      String filename = resource.getFilename();
                      String status = filename.substring(filename.length() - 10, filename.length() - 7);
                      MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                              .withHorizontalRuleCreateDocument(true)
                              .withIncludeBlockquote(false)
                              .withIncludeCodeBlock(false)
                              .withAdditionalMetadata("filename", filename)
                              .withAdditionalMetadata("status", status)
                              .build();
                      MarkdownDocumentReader markdownDocumentReader = new MarkdownDocumentReader(resource, config);
                      allDocuments.addAll(markdownDocumentReader.get());
                  }
              }catch (IOException e){
                  log.error("Markdown 文档加载失败",e);
              }
              return allDocuments;
          }
      
      ```

      得到以下结果，文档成功添加了元信息：

      ![1761969317391](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761969317391.png)

   3. 自动添加元信息，使用 Spring AI 提供的元信息 Transformer 组件，可以基于 AI 自动解析关键词并添加到元信息中。

      ```java
      @Component
      class MyKeywordEnricher {
          @Resource
          private ChatModel dashscopeChatModel;
      
          List<Document> enrichDocuments(List<Document> documents) {
              KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(this.dashscopeChatModel, 5);
              return enricher.apply(documents);
          }
      }
      
       @Bean
          VectorStore codingTeachAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
              SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                      .build();
              // 加载文档
              List<Document> documents = codingTeachAppDocumentReader.loadMarkdowns();
              // 这里使用关键词元信息增强器
              List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documents);
              simpleVectorStore.add(enrichDocuments);
              return simpleVectorStore;
          }
      
      ```

      效果如下，系统自动补充了相关标签：

      ![1761969711698](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761969711698.png)

#### 向量转换和存储

以下主要是一个技术选型的问题，就是你选择什么样的向量数据库以及合适的嵌入模型。

一般云平台上直接使用内置的向量存储或者云数据库。

选择嵌入模型时一般选择云平台提供的嵌入模型或者编程中看框架提供的模型。

#### 文档过滤和检索

1. 多查询扩展

   有时候用户输入的提示词可能不够完整，或者存在歧义。多查询扩展技术可以扩大检索范围，提高相关文档的召回率。

   使用时，要注意：

   - 设置合适的查询数量（建议 3 - 5 个），过多会影响性能、增大成本（因为这个是依赖 AI 来进行扩展的）
   - 保留原始查询的核心语义

   ```java
   @Component
   public class MultiQueryExpanderDemo {
   
       @Resource
       private ChatClient.Builder chatClientBuilder;
   
       public List<Query> expand(String query) {
           MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                   .chatClientBuilder(chatClientBuilder)
                   .numberOfQueries(3)
                   .build();
           List<Query> queries = queryExpander.expand(new Query(query));
           return queries;
       }
   }
   ```

   获得扩展的查询后，现在你可以拿多个查询来检索文档

   ```java
   DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
       .vectorStore(vectorStore)
       .similarityThreshold(0.73)
       .topK(5)
       .filterExpression(new FilterExpressionBuilder()
           .eq("genre", "fairytale")
           .build())
       .build();
   // 直接用扩展后的查询来获取文档
   List<Document> retrievedDocuments = documentRetriever.retrieve(query);
   // 输出扩展后的查询文本
   System.out.println(query.text());
   ```

   得到检索后的多个文档后，现在你需要将召回得到的多个文档进行合并和去重。

   最后再使用最终的文档改写 Prompt。

   但是一般不建议使用这个多查询扩展。

2. **查询重写和翻译**

   主要包括：

   - 使用 RewriteQueryTransformer 优化查询结构
   - 配置 TranslationQueryTransformer 支持多语言

   代码如下：

   ```java
   @Component
   public class QueryRewriter {
   
       private final QueryTransformer queryTransformer;
   
       public QueryRewriter(ChatModel dashscopeChatModel) {
           ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
           queryTransformer = RewriteQueryTransformer.builder()
                   .chatClientBuilder(builder)
                   .build();
       }
       public String doQueryRewrite(String query) {
           Query query1 = new Query(query);
           // 执行查询重写
           Query transform = queryTransformer.transform(query1);
           return transform.text();
       }
   }
   ```

   然后查询时使用重写后的 usermessage

   ```java
    public String doChatWithRag(String message,String chatId){
           // 查询重写
           String queryRewrite = queryRewriter.doQueryRewrite(message);
           ChatResponse chatResponse = chatClient
                   .prompt()
                   .user(queryRewrite)
   
   ```

   结果如下：

   ![1761971382603](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761971382603.png)

   在云平台上，你还可以开启多轮会话改写功能，自动将用户的提示词转换为更完整的形式。

3. **检索器配置**

   主要包括三个方面：相似度阈值、返回文档数量和过滤规则。（这个主要是给向量数据库使用的）

   我们可以自定义合适的 advisor 配置上给 RAG 使用的检索器，自定义相似度阈值、返回文档数量和过滤规则。

   代码如下：

   ```java
   **
    * 自定义的 RAG 检索增强顾问的工厂
    */
   @Slf4j
   public class CodingTeachAppRagCustomAdvisorsFactory {
       public static Advisor createCodingTeachAppRagCustomAdvisor(VectorStore vectorStore,String status){
   
           Filter.Expression expression = new FilterExpressionBuilder()
                   .eq("status", status)
                   .build();
   
   
           VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                   .vectorStore(vectorStore)
                   .similarityThreshold(0.5)
                   .topK(3)
                   .filterExpression(expression)
                   .build();
   
           return RetrievalAugmentationAdvisor.builder()
                   .documentRetriever(documentRetriever)
                   //.queryAugmenter()
                   .build();
       }
   }
   
   ```

   然后使用这个检索增强顾问进行查询

   ```java
    // 查询重写
           String queryRewrite = queryRewriter.doQueryRewrite(message);
           ChatResponse chatResponse = chatClient
                   .prompt()
                   .user(queryRewrite)
                   .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                           .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                   .advisors(new CodingLoggerAdvisor())
                   // 本地知识库 rag
                   //.advisors(new QuestionAnswerAdvisor(codingTeachAppVectorStore))
                   // 云上知识库 rag
                   .advisors(codingTeachAppRagCloudAdvisor)
                   // rag 基于pg远程向量数据库
                   //.advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                   .advisors(CodingTeachAppRagCustomAdvisorsFactory
                           .createCodingTeachAppRagCustomAdvisor(codingTeachAppVectorStore,"大学生"))
   
   ```

   执行查询的时候可以根据 debug 中的 rag 上下文来分辨是否这个 rag 检索增强顾问是否生效。



#### 查询增强和关联

在用户查询的时候，可能会出现找不到相关文档、相似度过低、查询超时等。

我们应该在出现如上情况的时候，允许空上下文查询、提供友好的错误提示，引导用户提供必要信息。

那么我们就可以创建一个自定义的查询上下文增强器

```java
/**
 * 创建上下文查询增强器的工厂
 */
public class CodingTeachAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createContextualQueryAugmenter() {
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答编程相关的问题，别的没办法帮到您哦，
                有问题可以联系编程导航客服 https://codefather.cn
                """);
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}

```

然后将其作为一个 rag 查询增强器的创建条件赋予

```java
return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)                .queryAugmenter(CodingTeachAppContextualQueryAugmenterFactory.createContextualQueryAugmenter())
                .build();
    }

```







## 5、工具调用

之前我们通过 RAG 技术让 AI 应用具备了根据外部知识库来获取信息并回答的能力，但是到目前为止，AI 应用还只是一个 “知识回答助手”。本阶段我们可以利用**工具调用**特性，实现更多需求。

- 联网搜索：比如智能推荐学习书籍、网站、视频
- 网页抓取：比如分析编程学习路线，看看网上的其他人时怎么学习编程的
- 资源下载：比如编程相关的资源，PDF 或者 代码等
- 终端操作：比如执行代码来生成编程指导报告
- 文件操作：比如保存自己的编程学习路线
- PDF 生成：比如编程学习计划 PDF 生成



### 工具调用介绍

#### 什么是工具调用

工具调用可以理解为让 AI 大模型**借用外部工具**来完成它自己做不到的事情。

工具可以是任何东西，比如网页搜索、对外部 API 的调用、访问外部数据、或执行特定的代码。

#### 工具调用的工作原理

其实，工具调用的工作原理很简单，**并不是 AI 服务器自己调用这些工具**、**也不是把工具的代码发送给 AI 服务器让它执行**，它智能提出要求，表示 “我需要执行 xx 工具来完成任务”。而真正执行工具的是我们自己的应用程序，执行后再把结果告诉 AI ，让它继续工作。

具体一点就是，用户提出问题，程序将问题传递给大模型，大模型分析问题（判断需要使用哪些工具来完成任务），大模型输出工具名称和参数，程序接收工具调用请求并执行操作，工具执行完成后将结果返回给大模型，大模型分析结果并生成回答，程序将大模型的回答返回给用户。

这个设计的关键一点就是**安全性**，AI 永远无法直接接触你的 API 或者系统资源，所有操作必须通过你的程序来执行。

#### 工具调用和功能调用

两者其实是一个东西。

#### 工具调用的技术选型

可以自主开发，但是更推荐使用 Spring AI、LangChain 等开发框架。



### Spring AI 工具开发

首先我们通过 Spring AI 官方提供的图片来理解 Spring AI 在实现工具调用时我们做了哪些事情

![1761993254610](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761993254610.png)

1. 工具定义和注册：通过框架的注解自动生成工具定义和 JSON Schema，让 Java 方法轻松成为 AI 可调用的工具。
2. 工具调用请求：框架自动处理与 AI 模型的通信并解析工具调用请求，支持多个工具链式调用。
3. 工具执行：框架提供统一的工具管理接口，自动根据 AI 返回的工具调用请求找到对应的工具并解析参数进行调用
4. 处理工具结果：框架内置结果转换和异常处理机制，支持各种复杂 Java 对象作为返回值并优雅处理错误。
5. 返回结果给模型：框架封装响应结果并管理上下文，确保工具执行结果正确传递给模型或直接返回给用户。
6. 生成最终响应：框架自动整合工具调用结果到对话上下文，支持多轮复杂交互，确保 AI 回复的连贯性和准确性。



#### 定义工具

在 Spring AI 中，主要有两种模式：基于 Methods 方法或者 Functions 函数式编程。

我们这里只使用**基于 Methods 方法**定义工具。

二者对比如下：

![1761993561439](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1761993561439.png)

举个例子，如下是 第一种 methods 模式：

```java
class WeatherTools {
    @Tool(description = "Get current weather for a location")
    public String getWeather(@ToolParam(description = "The city name") String city) {
        return "Current weather in " + city + ": Sunny, 25°C";
    }
}

// 使用方式
ChatClient.create(chatModel)
    .prompt("What's the weather in Beijing?")
    .tools(new WeatherTools())
    .call();
```

第二种函数式编程：

```java
@Configuration
public class ToolConfig {
    @Bean
    @Description("Get current weather for a location")
    public Function<WeatherRequest, WeatherResponse> weatherFunction() {
        return request -> new WeatherResponse("Weather in " + request.getCity() + ": Sunny, 25°C");
    }
}

// 使用方式
ChatClient.create(chatModel)
    .prompt("What's the weather in Beijing?")
    .functions("weatherFunction")
    .call();
```

#### 使用工具

1. 按需使用

   在构建 ChatClient 请求时通过 tools 方法附加工具。

   ```java
   String response = ChatClient.create(chatModel)
       .prompt("北京今天天气怎么样？")
       .tools(new WeatherTools())  // 在这次对话中提供天气工具
       .call()
       .content();
   ```

2. 全局使用

   如果工具需要在所有对话中都可以使用，可以在构建 ChatClient 时注册默认工具。

   ```java
   ChatClient chatClient = ChatClient.builder(chatModel)
       .defaultTools(new WeatherTools(), new TimeTools())  // 注册默认工具
       .build();
   
   ```

3. 更底层的使用方式

   可以给更底层的 ChatModel 绑定工具

   ```java
   // 先得到工具对象
   ToolCallback[] weatherTools = ToolCallbacks.from(new WeatherTools());
   // 绑定工具到对话
   ChatOptions chatOptions = ToolCallingChatOptions.builder()
       .toolCallbacks(weatherTools)
       .build();
   // 构造 Prompt 时指定对话选项
   Prompt prompt = new Prompt("北京今天天气怎么样？", chatOptions);
   chatModel.call(prompt);
   
   ```

4. 动态解析

   支持通过 `ToolCallbackResolver` 在运行时动态解析工具。适合工具需要根据上下文动态确定的场景。



### 主流工具开发

1. 文件操作

   ```java
   public class FileOperationTool {
   
       private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";
   
       @Tool(description = "Read content from a file")
       public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
           String filePath = FILE_DIR + "/" + fileName;
           try {
               return FileUtil.readUtf8String(filePath);
           } catch (Exception e) {
               return "Error reading file: " + e.getMessage();
           }
       }
   
       @Tool(description = "Write content to a file")
       public String writeFile(
               @ToolParam(description = "Name of the file to write") String fileName,
               @ToolParam(description = "Content to write to the file") String content) {
           String filePath = FILE_DIR + "/" + fileName;
           try {
               // 创建目录
               FileUtil.mkdir(FILE_DIR);
               FileUtil.writeUtf8String(content, filePath);
               return "File written successfully to: " + filePath;
           } catch (Exception e) {
               return "Error writing to file: " + e.getMessage();
           }
       }
   }
   
   ```

2. 联网搜索

   使用的是 Search API 的方法

   ```java
   public class WebSearchTool {
   
       // SearchAPI 的搜索接口地址
       private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";
   
       private final String apiKey;
   
       public WebSearchTool(String apiKey) {
           this.apiKey = apiKey;
       }
   
       @Tool(description = "Search for information from Baidu Search Engine")
       public String searchWeb(
               @ToolParam(description = "Search query keyword") String query) {
           Map<String, Object> paramMap = new HashMap<>();
           paramMap.put("q", query);
           paramMap.put("api_key", apiKey);
           paramMap.put("engine", "baidu");
           try {
               String response = HttpUtil.get(SEARCH_API_URL, paramMap);
               // 取出返回结果的前 5 条
               JSONObject jsonObject = JSONUtil.parseObj(response);
               // 提取 organic_results 部分
               JSONArray organicResults = jsonObject.getJSONArray("organic_results");
               List<Object> objects = organicResults.subList(0, 5);
               // 拼接搜索结果为字符串
               String result = objects.stream().map(obj -> {
                   JSONObject tmp = (JSONObject) obj;
                   JSONObject filtered = new JSONObject();
                   // 提取需要的三个字段，不存在时可能为null，但不影响序列化
                   filtered.put("title", tmp.getStr("title"));
                   filtered.put("link", tmp.getStr("link"));
                   filtered.put("snippet", tmp.getStr("snippet"));
                   return filtered.toString();
               }).collect(Collectors.joining(","));
               return result;
           } catch (Exception e) {
               return "Error searching Baidu: " + e.getMessage();
           }
       }
   }
   
   ```

3. 网页抓取

   ```java
   public class WebScrapingTool {
   
       @Tool(description = "Scrape the content of a web page")
       public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
           try {
               Document doc = Jsoup.connect(url).get();
               return doc.html();
           } catch (IOException e) {
               return "Error scraping web page: " + e.getMessage();
           }
       }
   }
   
   ```

4. 终端操作

   ```java
   public class TerminalOperationTool {
   
       @Tool(description = "Execute a command in the terminal")
       public String executeTerminalCommand(@ToolParam(description = "Command to execute in the terminal") String command) {
           StringBuilder output = new StringBuilder();
           try {
               ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
   //            Process process = Runtime.getRuntime().exec(command);
               Process process = builder.start();
               try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                   String line;
                   while ((line = reader.readLine()) != null) {
                       output.append(line).append("\n");
                   }
               }
               int exitCode = process.waitFor();
               if (exitCode != 0) {
                   output.append("Command execution failed with exit code: ").append(exitCode);
               }
           } catch (IOException | InterruptedException e) {
               output.append("Error executing command: ").append(e.getMessage());
           }
           return output.toString();
       }
   }
   ```

5. 资源下载

   ```java
   public class ResourceDownloadTool {
   
       @Tool(description = "Download a resource from a given URL")
       public String downloadResource(@ToolParam(description = "URL of the resource to download") String url, @ToolParam(description = "Name of the file to save the downloaded resource") String fileName) {
           String fileDir = FileConstant.FILE_SAVE_DIR + "/download";
           String filePath = fileDir + "/" + fileName;
           try {
               // 创建目录
               FileUtil.mkdir(fileDir);
               // 使用 Hutool 的 downloadFile 方法下载资源
               HttpUtil.downloadFile(url, new File(filePath));
               return "Resource downloaded successfully to: " + filePath;
           } catch (Exception e) {
               return "Error downloading resource: " + e.getMessage();
           }
       }
   }
   ```

6. PDF 生成

   ```java
   public class PDFGenerationTool {
   
       @Tool(description = "Generate a PDF file with given content")
       public String generatePDF(
               @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
               @ToolParam(description = "Content to be included in the PDF") String content) {
           String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
           String filePath = fileDir + "/" + fileName;
           try {
               // 创建目录
               FileUtil.mkdir(fileDir);
               // 创建 PdfWriter 和 PdfDocument 对象
               try (PdfWriter writer = new PdfWriter(filePath);
                    PdfDocument pdf = new PdfDocument(writer);
                    Document document = new Document(pdf)) {
                   // 自定义字体（需要人工下载字体文件到特定目录）
   //                String fontPath = Paths.get("src/main/resources/static/fonts/simsun.ttf")
   //                        .toAbsolutePath().toString();
   //                PdfFont font = PdfFontFactory.createFont(fontPath,
   //                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                   // 使用内置中文字体
                   PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                   document.setFont(font);
                   // 创建段落
                   Paragraph paragraph = new Paragraph(content);
                   // 添加段落并关闭文档
                   document.add(paragraph);
               }
               return "PDF generated successfully to: " + filePath;
           } catch (IOException e) {
               return "Error generating PDF: " + e.getMessage();
           }
       }
   }
   
   ```



#### 集中注册

统一管理和绑定所有工具。

```java
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        return ToolCallbacks.from(
            fileOperationTool,
            webSearchTool,
            webScrapingTool,
            resourceDownloadTool,
            terminalOperationTool,
            pdfGenerationTool
        );
    }
}
```

#### 使用工具

```java
@Resource
private ToolCallback[] allTools;

public String doChatWithTools(String message, String chatId) {
    ChatResponse response = chatClient
            .prompt()
            .user(message)
            .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                    .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
            // 开启日志，便于观察效果
            .advisors(new MyLoggerAdvisor())
            .tools(allTools)
            .call()
            .chatResponse();
    String content = response.getResult().getOutput().getText();
    log.info("content: {}", content);
    return content;
}
```







## 6、MCP 协议

### MCP 必知必会

#### 什么是 MCP？

MCP （Model Context Protocol，模型上下文协议）是一种开放**标准**，目的是增强 AI 与外部系统的交互能力。

它是一个**协议或者标准**，本身不提供什么服务，只是定义好了一套规范，让服务提供者和服务使用者去遵守。

那么 MCP 为什么突然火起来了呢？工具调用其实也存在有几年了，它为什么没有火起来呢？

其实工具调用面向的更多是我们程序员本身，它并不面向全部用户，而 MCP 如果一个用户直接去 MCP 市场上搜索用法下载安装就可以使用了。

#### MCP 架构

1. **宏观架构**

   核心是 “客户端 - 服务器” 架构。

   <img src="C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1762056887994.png" alt="1762056887994" style="zoom:50%;" />

2. **SDK 架构**

   <img src="C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1762057139562.png" alt="1762057139562" style="zoom:50%;" />

   

   - 客户端 / 服务器层：处理客户端操作，管理服务器端协议操作。两者通过 session 进行通信管理。
   - 会话层：通过 DefaultMcpSession 实现管理通信模式和状态。
   - 传输层：处理 JSON-RPC 消息序列化和反序列化，支持多种传输实现，比如 Stdio 标准 IO 流传输和 HTTP SSE 远程传输。



### 使用 MCP

