## AI 超级智能体流程梳理

### AI 大模型接入

首先要使用 AI 大模型的话，我们要确定一件事情，那就是我们要用 AI 大模型去处理哪些事情，比如文本、图像、视频又或者是音频之类的东西，不同的大模型对于不同类型的数据处理能力是不一样的，选择合适的大模型才能做对的事情。以及大模型支持的功能，可以使用工具调用以及联网的能力。还有生态以及社区的维护能力。

接下来，在合适的大模型中，选择一个性价比适中的大模型（这里需要考虑大模型的 token 的费用，包括输入和输出的），然后考虑怎么样使用大模型，是使用大模型平台提供的服务，还是利用大模型提供的 SDK 或者接口，又或者是利用 AI 开发框架来使用大模型。

然后重要的是要掌握提示词工程以及大模型的开发工作流。

#### 接入大模型的方式

1. AI 应用平台接入

   可以在云平台上直接使用厂商提供的不同大模型以及创建属于自己的智能体应用。

   阿里云现在只提供三种，一种是智能体（可以理解为大模型加rag、mcp、工具等等的集合，只能在这个大模型的范围里面做事情和加强），一种是工作流（变量输入进来，然后经过很多步流程，流程中可以添加大模型、知识库、mcp、应用组件、api、插件、函数计算、脚本等操作），还有最后一种是高代码应用（开发者可以将 python 项目部署为云上 API ，并且该项目支持自动运维、日志、可观测服务）

2. AI 软件客户端。

   比如一些内置了大模型的软件，Cherry Studio、cursor

3. 程序接入

   直接调用 AI 大模型，比如调用 DeepSeek （更原生）

   调用 AI 大模型平台创建的应用或者智能体

4. 本地下载大模型。

   在本地安装部署大模型，然后使用，一般通过 ollama 快速安装大模型



#### 程序调用 AI 大模型

1. 使用官方提供的 SDK 或者 API。
2. 可以直接使用 HTTP 直接调用大模型的 API。（可以将官网提供的 HTTP 请求转换为自己语言的代码）
3. 使用 Spring AI 开发框架
4. 使用 LangChain4j 开发框架





### AI 应用开发

#### 提示词工程

提示词的质量直接影响到 AI 大模型的输出结果。

提示词也分不同的类型。最重要的我感觉是角色的分类：

1. 系统提示词

   给大模型的提示词，规定了大模型的角色定位和可以干什么和怎样干的范围和限制。

2. 用户提示词。

   用户输入给大模型的提示词，这是向大模型提出的实际问题，就是要大模型干什么事情。

3. 助手提示词。

   大模型接受用户输入后，返回给用户的响应。会被作为对话上下文拼接到下一次的对话中。

其实还存在一些功能上的，以及复杂度上的分类。主要就是一些引导性质的对话，或者规定好结构的对话以及对话简单复杂程度的对话。



Token 是大模型处理文本的基本单位。输入和输出都要消耗 token。一般 100 个英文单词约等于 75 - 150 个 Token，100 个中文约等于 100 - 200 个 Token。

不过具体的数量可以按照不同大模型官网提供的专门的 token 计算器来进行计算。

那么我们同样可以优化 token 的输入和输出来做到降低成本，一般可以精简输入 token，提炼关键内容，定期清理对话历史，因为之前的对话历史会被作为对话上下文拼接到下一次的对话中，还有使用向量数据库，避免查询的时候讲整个文档全部作为查询内容的附加，还有使用结构化的语言替代自然语言。



当然提示词有专门的学习流程，各大主流模型和开发框架都有相关的介绍

Spring AI 的 prompt 有两个属性，一个 messages，一个 chatOptions

```java
public class Prompt implements ModelRequest<List<Message>> {

    private final List<Message> messages;

    private ChatOptions chatOptions;
}
```

Message 封装了具体的 content，以及内容的具体类型（用户、系统、助手）

content 就是具体的内容以及元信息。

```java
public interface Content {

	String getContent();

	Map<String, Object> getMetadata();
}

public interface Message extends Content {

	MessageType getMessageType();
}
```

另外还有除了文本类型的数据，mediacontent

```java
public interface MediaContent extends Content {

	Collection<Media> getMedia();

}
```

具体的关系如下图所示：

![Spring AI 消息 API](https://docs.spring.io/spring-ai/reference/_images/spring-ai-message-api.jpg)

 **提示词模板**

 PromptTemplate 类提供了可以动态替换提示词的方法。

```java
PromptTemplate promptTemplate = PromptTemplate.builder()
    .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
    .template("""
            Tell me the names of 5 movies whose soundtrack was composed by <composer>.
            """)
    .build();

String prompt = promptTemplate.render(Map.of("composer", "John Williams"));
```



同样网上也存在很多现成的提示词库，用于专门的文本对话或者 AI 绘画（专门的领域中）。

**提示词技巧**

总结起来为下面几点：

- 明确指定任务和角色
- 提供详细说明和具体示例
- 使用结构化格式引导思维
- 明确输出格式要求
- 思维链提示法（COT）引导模型展示推理过程，逐步思考问题
- 少样本学习 。提供几个对话例子，让它按照这个样式输出
- 分步骤指导。将复杂任务拆分为几个步骤
- 自我评估和修正
- 知识检索和引用。确保输出的内容是真实可信并且相关的
- 多视角分析。从不同角度或角色来分析一件事情
- 多模态思维

总结出来就一句话：**任务越复杂，就越要给提示词补充更多细节。**

#### AI 应用分析

我们可以让 AI 给我们涉及应用的系统提示词，给出自己要完成的应用和要求，然后让 AI 帮你生成给大模型用的系统提示词。

#### 多轮对话实现

要实现具有 “记忆力” 的 AI 应用，让 AI 能够记住用户之前的对话内容并保持上下文连贯性，我们可以使用 Spring AI 框架的 **对话记忆能力。**

可以使用 Spring AI 提供的 **ChatClient API** 来和 AI 大模型交互。

你可以先自动注入一个 ChatClient 用来后续跟大模型的交互。

```java
public CodingTeachApp(ChatModel dashscopeChatModel) {
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new CodingLoggerAdvisor())
                .build();
    }
```

ChatClient 你可以看做是之前大模型平台上提供的智能体应用。你可以设置一些额外的配置，比如默认的系统提示词、用户提示词、rag增强、advisor 等等。

同样 ChatClient 也支持很多种响应返回格式。比如返回 ChatResponse 对象、实体对象、流式返回以及 content：

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

Spring AI 使用 Advisors（顾问） 机制来增强 AI 的能力，可以理解为一系列可插拔的拦截器，在调用 AI 前和调用 AI 后可以执行一些额外的操作。

用法很简单，我们可以直接为 ChatClient 指定默认拦截器，比如对话记忆拦截器  Me⁠ssageChatMemoryAdv⁠isor  可以帮助我们实现多轮对话能力，省去了自己维护对话列表的麻烦。

advisor 的处理流程就是用户的 prompt 进来之后，会成为一个 advisedRequest 先经过 advisor 的处理，然后跟大模型进行交互，得到响应后，再交给 advisor 处理得到 advisedResponse 然后再转换成为 ChatResponse。

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

 具体的继承关系如下图所示，主要是用到了一个责任链的设计模式，order 用来表示每个 advisor 的执行优先级，优先级越低的执行优先级越高。然后具体的 advisor 最好实现 callAdvisor 和 streamAdvisor 两个接口，用来处理流式和非流式的数据。![顾问 API 类](https://docs.spring.io/spring-ai/reference/_images/advisors-api-classes.jpg) 

下面是一个日志记录拦截器的实现：

```java
public class SimpleLoggerAdvisor implements CallAdvisor, StreamAdvisor {

	private static final Logger logger = LoggerFactory.getLogger(SimpleLoggerAdvisor.class);

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public int getOrder() {
		return 0;
	}


	@Override
	public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
		logRequest(chatClientRequest);

		ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);

		logResponse(chatClientResponse);

		return chatClientResponse;
	}

	@Override
	public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest,
			StreamAdvisorChain streamAdvisorChain) {
		logRequest(chatClientRequest);

		Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);

		return new ChatClientMessageAggregator().aggregateChatClientResponse(chatClientResponses, this::logResponse);
	}

	private void logRequest(ChatClientRequest request) {
		logger.debug("request: {}", request);
	}

	private void logResponse(ChatClientResponse chatClientResponse) {
		logger.debug("response: {}", chatClientResponse);
	}

}
```

Spring AI 内置了非常多的 advisor，常用的有如下的：

- `MessageChatMemoryAdvisor`

  检索记忆并将其作为消息集合添加到提示中。这种方法可以保持对话历史的结构。请注意，并非所有 AI 模型都支持这种方法。

  它需要传入一个 chatMemory 进行初始化，其实这里的 chatMemory 具体是  MessageWindowChatMemory 类的，MessageWindowChatMemory 需要一个数据源层面的抽象层来决定持久化的时候具体执行怎样的持久化逻辑，其实也就是简单的 add，get，delete 方法，具体的实现根据你的配置和选择的持久化方式可以自己进行实现。

  ```java
  ChatMemoryRepository chatMemoryRepository = JdbcChatMemoryRepository.builder()
      .jdbcTemplate(jdbcTemplate)
      .dialect(new PostgresChatMemoryRepositoryDialect())
      .build();
  
  ChatMemory chatMemory = MessageWindowChatMemory.builder()
      .chatMemoryRepository(chatMemoryRepository)
      .maxMessages(10)
      .build();
  ```

- `PromptChatMemoryAdvisor`

  从内存中检索信息并将其合并到提示的系统文本中。

- `VectorStoreChatMemoryAdvisor`

  从 VectorStore 中检索内存并将其添加到提示的系统文本中。此顾问程序可用于高效地从大型数据集中搜索和检索相关信息。

- `QuestionAnswerAdvisor`

  该顾问使用向量存储来提供问答功能，实现了朴素 RAG（检索增强生成）模式。

- `RetrievalAugmentationAdvisor`

  ```
  顾问使用 `org.springframework.ai.rag` 包中定义的构建块，并遵循模块化 RAG 架构，实现常见的检索增强生成 (RAG) 流程。
  ```

- `ReReadingAdvisor`

  针对 LLM 推理实施了一种名为 RE2 的重读策略，以增强输入阶段的理解。基于文章：[重读可提高 LLM 推理能力]( [arxiv.org/pdf/2309.06275](https://arxiv.org/pdf/2309.06275) )。

  本质上其实就是给设置了一个 promptTemplate，然后在 advisor 执行的时候，给用户提示词使用这个增强顾问，把用户信息输入了两边结合到一个当中。

  ```java
  public class ReReadingAdvisor implements BaseAdvisor {
  
  	private static final String DEFAULT_RE2_ADVISE_TEMPLATE = """
  			{re2_input_query}
  			Read the question again: {re2_input_query}
  			""";
  
  	private final String re2AdviseTemplate;
  
  	private int order = 0;
  
  	public ReReadingAdvisor() {
  		this(DEFAULT_RE2_ADVISE_TEMPLATE);
  	}
  
  	public ReReadingAdvisor(String re2AdviseTemplate) {
  		this.re2AdviseTemplate = re2AdviseTemplate;
  	}
  
  	@Override
  	public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
  		String augmentedUserText = PromptTemplate.builder()
  			.template(this.re2AdviseTemplate)
  			.variables(Map.of("re2_input_query", chatClientRequest.prompt().getUserMessage().getText()))
  			.build()
  			.render();
  
  		return chatClientRequest.mutate()
  			.prompt(chatClientRequest.prompt().augmentUserMessage(augmentedUserText))
  			.build();
  	}
  
  	@Override
  	public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
  		return chatClientResponse;
  	}
  
  	@Override
  	public int getOrder() {
  		return this.order;
  	}
  
  	public ReReadingAdvisor withOrder(int order) {
  		this.order = order;
  		return this;
  	}
  
  }
  ```

- `SafeGuardAdvisor`

  一个简单的顾问，旨在防止模型生成有害或不恰当的内容。



1）Messag‌eChatMemoryAdvi⁡sor 将对话历史作为一系列独⁠立的消息添加到提示中，保留原始⁠对话的完整结构，包括每条消息的؜角色标识（用户、助手、系统）。

```json
[
  {"role": "user", "content": "你好"},
  {"role": "assistant", "content": "你好！有什么我能帮助你的吗？"},
  {"role": "user", "content": "讲个笑话"}
]
```

2）Prom‌ptChatMemor⁡yAdvisor 将对⁠话历史添加到提示词的系⁠统文本部分，因此可能会؜失去原始的消息边界。

```json
以下是之前的对话历史：
用户: 你好
助手: 你好！有什么我能帮助你的吗？
用户: 讲个笑话

现在请继续回答用户的问题。
```





#### 结构化输出

该`StructuredOutputConverter`接口允许您获取结构化输出，例如将基于文本的 AI 模型输出映射到 Java 类或值数组。 

```java
public interface StructuredOutputConverter<T> extends Converter<String, T>, FormatProvider {
}
```

下图为具体工作流程：

![结构化输出 API](https://docs.spring.io/spring-ai/reference/_images/structured-output-api.jpg)

一般会使用 PromptTemplate 附加到用户输入的末尾：

    StructuredOutputConverter outputConverter = ...
    String userInputTemplate = """
        ... user text input ....
        {format}
        """; // user input with a "format" placeholder.
    Prompt prompt = new Prompt(
            PromptTemplate.builder()
    					.template(this.userInputTemplate)
    					.variables(Map.of(..., "format", this.outputConverter.getFormat())) // replace the "format" placeholder with the converter's format.
    					.build().createMessage()
    ); 

#### 对话记忆持久化

项目中的 chatMemory 是自定义实现的，实现了将会话持久化保存到数据库中的方法。其实可以参考 ChatMemory 的实现类，比如 InMemoryChatMemory。其实就是自己维护了一个 map 集合，key 为会话 id，value 就是历史对话的集合。

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/ijWNltU3tpmgbUFW.webp) 

具体实现如下图所示：

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



### RAG 知识库基础

RAG （Retrieval - Augmented Generation）检索增强生成技术，是一种结合信息检索技术和 AI 内容生成的混合架构，可以解决大模型的知识时效性限制和幻觉问题。

简单来说，原有的大模型受限于训练数据的时效化以及知识的不全面，可能无法输出最新的知识以及用户对于特定领域的知识的询问，可能输出的是已经过时的消息以及幻觉的消息，所以需要给大模型提供一个特定领域的知识库，可以让大模型根据用户的询问去知识库里面查询到相关的知识，并将其添加到用户的提示词当中作为对话上下文来确保输出的知识的最新以及真实有效性。

#### RAG 的流程

1. 文档收集和切割

   需要先收集特定领域的相关知识文档，然后把它们按照大模型可以接受下的 token 数量按照语义或者片段进行切割。

2. 向量转换和存储

   切割后的文档可以使用 Embedding 模型进行向量转换，将文本转换成为高维度的向量数据，并且把其存入到专门的向量数据库中，方便查询时根据向量相似度或者欧氏距离等算法快速查找出有关联的文档片段。

3. 文档过滤和检索

   文档查询出来后，可能文档本身带有某些元数据以及相似度，我们肯定需要的是相关性最强的几组文档，以及可能设置了某些过滤条件，所以需要进行过滤以及 topK 的排序查找出最相关的文档，这里面混合检索指的是多种检索方式融合（不止是向量检索、全文检索，只要有混合使用都能称之为混合检索）

4. 查询增强和关联

   将查询出的 topK 的文档拼接到用户的提示词当中作为上下文输入给大模型

#### RAG 实战

1. 首先可以准备相关的文档，最好是 md 格式的。

2. 然后是文档读取，对准备好的文档进行读取和处理，然后保存到向量数据库中。这个过程称之为 ETL（抽取、转换、加载）

   以下是 ETL 的三大核心组件，按照顺序执行：

   - DocumentReader：读取文档，得到文档列表
   - DocumentTransformer：转换文档，得到处理后的文档列表
   - DocumentWriter：将文档列表保存到存储中

   ![img](https://pic.code-nav.cn/course_picture/1608440217629360130/dyTCTOQSoVRoLRS7.webp) 

```java
public interface DocumentReader extends Supplier<List<Document>> {

    default List<Document> read() {
		return get();
	}
}

public interface DocumentTransformer extends Function<List<Document>, List<Document>> {

    default List<Document> transform(List<Document> transform) {
		return apply(transform);
	}
}

public interface DocumentWriter extends Consumer<List<Document>> {

    default void write(List<Document> documents) {
		accept(documents);
	}
}
```

Spring AI 提供了很多种文档阅读器，只需要表明资源所在位置以及具体的读取规则就可以：

```java
@Component
class MyMarkdownReader {

    private final Resource resource;

    MyMarkdownReader(@Value("classpath:code.md") Resource resource) {
        this.resource = resource;
    }

    List<Document> loadMarkdown() {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
            .withHorizontalRuleCreateDocument(true)
            .withIncludeCodeBlock(false)
            .withIncludeBlockquote(false)
            .withAdditionalMetadata("filename", "code.md")
            .build();

        MarkdownDocumentReader reader = new MarkdownDocumentReader(this.resource, config);
        return reader.get();
    }
}
```

`MarkdownDocumentReaderConfig`允许您自定义 MarkdownDocumentReader 的行为：

- `horizontalRuleCreateDocument`：当设置为 时`true`，Markdown 中的水平线将创建新`Document`对象。
- `includeCodeBlock`：设置为 true 时，代码块将与周围的文本`true`包含在同一个对象中。设置为 false 时，代码块将创建单独的对象。`Document``false``Document`
- `includeBlockquote`：当设置为 true 时`true`，引用块将包含在`Document`周围文本中。当设置为 false 时`false`，引用块将创建单独的`Document`对象。
- `additionalMetadata`允许您向所有创建的对象添加自定义元数据`Document`。

我们编写一个文档加载器类  LoveAppDocumentLoader ，负责读取所有资源路径上的 Markdown 文档并转换为 Document 列表。代码如下：

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
}
```

上述代码中，我们通过  Mar‌kdownDocumentReaderConfig  文档加载配置来指定读取文档的细节，比如是否读取代码块、引用块。特别需要注意的是，我们还指定了额外的元信息配置，提取文档的文件名（fileName）作为文档的元信息，可以便于后续知识库实现更精确的检索。

然后，我们使用 Spri⁡ng AI 内置的、基⁠于内存读写的向量数据库⁠ SimpleVect؜orStore 来保存文档。 

SimpleVect؜orStore 实现了 VectorStore 接口，而 VectorStore 接口集成了  DocumentWriter ，所以具备文档写入能力。

我们通过 SimpleVect؜orStore 实现初始化向量数据库并且保存文档的方法。

```java
@Configuration
public class CodingTeachAppVectorConfig {

    @Resource
    private CodingTeachAppDocumentReader codingTeachAppDocumentReader;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    VectorStore codingTeachAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel)
                .build();
        // 加载文档
        List<Document> documents = codingTeachAppDocumentReader.loadMarkdowns();
        // 这里使用关键词元信息增强器
        List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documents);
        //simpleVectorStore.doAdd(enrichDocuments);
        simpleVectorStore.add(enrichDocuments);
        return simpleVectorStore;
    }

}
```

Spring AI 通过 Advisor 特性提供了开箱即用的 RAG 功能。主要是  QuestionAnswerAdv⁠isor  问答拦截器和  RetrievalAug⁠mentationAdvisor 检索增强拦截器，前者更简单易用、后者更灵活强大。

查询增强的原理很简单，当用户问题发送给 AI 的时候，QuestionAnswerAdv⁠isor  会去查询向量数据库，获取与用户问题相关的文档，然后这个文档会被附加到用户提示词中，当作对话上下文，帮助其生成回答。

这里使用了简单的 QuestionAnswerAdvisor 方法。

```java
 public String doChatWithRag(String message,String chatId){
        // 查询重写
        String queryRewrite = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(queryRewrite)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new CodingLoggerAdvisor())
                // 本地知识库 rag
                .advisors(new QuestionAnswerAdvisor(codingTeachAppVectorStore))
```

#### RAG 云上知识库

代码如下：

这里使用的是另一个查询增强拦截器 RetrievalAugmentationAdvisor，使用的是构造了一个 DashScopeDocumentRetriever Spring AI Alibaba 实现的一个文档检索器。

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





### RAG 知识库进阶

RAG 的核心流程：

- 文档收集和抽取
- 向量转换和存储
- 向量检索和过滤
- 查询增强和关联

#### 文档的收集和切割 ETL

第一步文档的收集和抽取就是 ETL 中的第一步，关键就是先准备好需要的文档（这里分文本数据和多媒体类型），然后如果要使用官方提供的 DocumentReader 的话基本就是确定好文档的资源路径以及相对应 DocumentReaderConfig 的配置（这里就是文档如何抽取成为 Document 的规则），如果你想实现官方没有提供的 DocumentReader 的话，只要实现这个 DocumentReader 接口，然后重写 get 方法即可，get 方法其实就是把文档里面的数据读取然后转换成为 Document 的过程。当然读取的时候你可以给 Document 加上一些 metadata，不限于名字和一些状态信息。

ETL的第二步就是文档的转换。转换就是把之前读取的 Document 按照固定大小、语义或者递归的规则进行划分，划分的目的是为了保证每个 document 的大小首先是大模型可以进行读取的长度大小，然后每个划分后的片段的语义最好保持一致。可以使用官方提供的 DocumentTransformer 的实现。基本有 textSplitter（它的实现有 tokenTextSplitter，你可以给它设置一些初始化参数，让它按照最少块大小，最少块数量，划分规则等进行划分）这是一个基于文本的分割器；还有 MetadataEnricher 元数据增强器，这是为了给划分后的文档切片添加一些补充信息，而不是改变原本的划分规则（实现类分为 keyWordMetadataEnricher 使用 AI 提取关键词添加到元数据，还有一个SummaryMetadataEnricher，使用 AI 总结摘要添加到元数据，可以关联前一个和后一个相邻的文档）；还有一个 ContentFormatter 它的作用可以把元数据和文档结合到一起，排除特定的元数据然后提供给大模型结构化的输入。

ETL 的第三步就是文档的加载。Spring AI 通过 DocumentWriter 组件实现文档加载（写入）。提供了两种实现，第一个是 FileDocumentWriter，将文档写入到文件系统，还有一种就是 VectorStoreWriter，将文档写入到向量数据库中。

将上述 3 大组件结合起来，就是完整的 ETL 流程：

先读取文档为 document，然后进行切分和文档增强，最后将 document 切片存储到向量数据库中。

```java
// 抽取：从 PDF 文件读取文档
PDFReader pdfReader = new PagePdfDocumentReader("knowledge_base.pdf");
List<Document> documents = pdfReader.read();

// 转换：分割文本并添加摘要
TokenTextSplitter splitter = new TokenTextSplitter(500, 50);
List<Document> splitDocuments = splitter.apply(documents);

SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(chatModel, 
    List.of(SummaryType.CURRENT));
List<Document> enrichedDocuments = enricher.apply(splitDocuments);

// 加载：写入向量数据库
vectorStore.write(enrichedDocuments);

// 或者使用链式调用
vectorStore.write(enricher.apply(splitter.apply(pdfReader.read())));

```

#### 向量转换和存储

向量存储是 RAG 应用中的核心组件，它将文档转换为向量（嵌入）并存储起来。

Spring AI 提供了向量数据库接口 VectorStore 和向量存储整合包，帮助快速集成各种第三方向量存储。

下面是 VectorStore 接口，它继承自 DocumentWriter：

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

其实就是定义了向量存储的基本操作，简单来说就是 “增删改查”：

- 添加文档到向量库
- 从向量库删除文档
- 基于查询进行相似度搜索
- 获取原生客户端（用于特定实现的高级操作）

其中相似度搜索的时候，可以看到参数是一个 SearchRequest 类，它提供了多种配置选项，query 搜索的查询文本，topK 返回的最大结果数，similarityThreshold 相似度阈值，低于此值的会被过滤掉，filterExpression 基于文档元数据的过滤表达式，语法有点类似 SQL 语句

下面是 SearchRequest 的示例：

```java
SearchRequest request = SearchRequest.builder()
    .query("什么是程序员鱼皮的编程导航学习网 codefather.cn？")
    .topK(5)                  // 返回最相似的5个结果
    .similarityThreshold(0.7) // 相似度阈值，0.0-1.0之间
    .filterExpression("category == 'web' AND date > '2025-05-03'")  // 过滤表达式
    .build();

List<Document> results = vectorStore.similaritySearch(request);
```

基于 PGVector 实现向量存储：

PGVector 是经典数据库 PostgreSQL 的扩展，为 PostgreSQL 提供了存储和检索高维向量数据的能力。

这里有两种实现方式，一种是本地服务器安装 PostgreSQL ，另一种就是使用云数据库。

我们这里使用的是阿里云提供的 PostgreSQL ，需要开通后安装 vector 插件以及开启公网访问地址。

注意配置的编写：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://改为你的公网地址/yu_ai_agent
    username: 改为你的用户名
    password: 改为你的密码
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW
        dimensions: 1536
        distance-type: COSINE_DISTANCE
        max-document-batch-size: 10000 # Optional: Maximum number of documents per batch
```

随后自己编写配置类构造 PgVectorStore：

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
        // 加载文档
        List<Document> documents = codingTeachAppDocumentReader.loadMarkdowns();
        vectorStore.add(documents);
        return vectorStore;
    }

}
```

在存储文档时，可能要一次性进行大量文档的向量转换，可能会导致性能问题、甚至出现错误导致数据不完整。

为此，Spring AI 实现了批处理策略，将大量文档分解为较小的批次，使其适合嵌入模型的最大上下文串口。

通过 BatchingStrategy 接口提供功能, Spring AI 提供了一‌个名为 TokenCountBatchingStrate⁡gy 的默认实现 。这个策略为每个文档估算 token 数⁠，将文档分组到不超过最大输入 token 数的批次中，如⁠果单个文档超过此限制，则抛出异常。这样就确保了每个批次不؜超过计算出的最大输入 token 数。 

```java
@Configuration
public class EmbeddingConfig {
    @Bean
    public BatchingStrategy customTokenCountBatchingStrategy() {
        return new TokenCountBatchingStrategy(
            EncodingType.CL100K_BASE,  // 指定编码类型
            8000,                      // 设置最大输入标记计数
            0.1                        // 设置保留百分比
        );
    }
}
```

#### 文档的过滤和检索

Spring AI 官方声称提供了一个 “模块化” 的 RAG 结构，用于优化大模型回复的准确性。

简单来说，就是把整个文档过滤检索阶段拆分为：检索前、检索时、检索后，针对每个阶段都提供了可自定义的组件。

- 在预检索阶段，系统接收用户的原始查询，通过查询转换和查询扩展等方法对其进行优化，输出增强的用户查询。
- 在检索阶段，系统使用增强的查询从知识库中搜索相关文档，可能涉及多个检索源的合并，最终输出一组相关文档。
- 在检索后阶段，系统对检索到的文档进行进一步处理，包括排序、选择最相关的子集以及压缩文档内容，输出经过优化的相关文档集。

**预检索**：

1. 查询转换 - 查询重写

   `RewriteQueryTransformer` 使用大语言模型对用户的原始查询进行改写，使其更加清晰和详细。当用户查询含糊不清或包含无关信息时，这种方法特别有用。 

   ```java
   Query query = new Query("啥是程序员橙汁啊啊啊啊？");
   
   QueryTransformer queryTransformer = RewriteQueryTransformer.builder()
           .chatClientBuilder(chatClientBuilder)
           .build();
   
   Query transformedQuery = queryTransformer.transform(query);
   ```

   实现原理也很简单，给大模型提供了一个改写查询的提示词

   ```java
   private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("""
   			Given a user query, rewrite it to provide better results when querying a {target}.
   			Remove any irrelevant information, and ensure the query is concise and specific.
   
   			Original query:
   			{query}
   
   			Rewritten query:
   			""");
   ```

2. 查询转换 - 查询翻译

   `TranslationQueryTransformer` 将查询翻译成嵌入模型支持的目标语言。如果查询已经是目标语言，则保持不变。这对于嵌入模型是针对特定语言训练而用户查询使用不同语言的情况非常有用，便于实现国际化应用。 

   同理也是给了一段提示词，让大模型将目标查询转换成目标语言。不过一般不建议使用这个方法，因为调用大模型的成本远比调用第三方翻译 API 的成本要高。

   ```java
   Query query = new Query("hi, who is coder yupi? please answer me");
   
   QueryTransformer queryTransformer = TranslationQueryTransformer.builder()
           .chatClientBuilder(chatClientBuilder)
           .targetLanguage("chinese")
           .build();
   
   Query transformedQuery = queryTransformer.transform(query);
   
   ```

3. 查询转换 - 查询压缩

   `CompressionQueryTransformer` 使用大语言模型将对话历史和后续查询压缩成一个独立的查询，类似于概括总结。适用于对话历史较长且后续查询与对话上下文相关的场景。 

   原理也是给了一段提示词让把历史对话和后续查询压缩再进行查询

   ```java
   Query query = Query.builder()
           .text("编程导航有啥内容？")
           .history(new UserMessage("谁是程序员鱼皮？"),
                   new AssistantMessage("编程导航的创始人 codefather.cn"))
           .build();
   
   QueryTransformer queryTransformer = CompressionQueryTransformer.builder()
           .chatClientBuilder(chatClientBuilder)
           .build();
   
   Query transformedQuery = queryTransformer.transform(query);
   ```

4. 查询扩展 - 多查询扩展

   `MultiQueryExpander` 使用大语言模型将一个查询扩展为多个语义上不同的变体，有助于检索额外的上下文信息并增加找到相关结果的机会。就理解为我们在网上搜东西的时候，可能一种关键词搜不到，就会尝试一些不同的关键词。 

   ```java
   MultiQueryExpander queryExpander = MultiQueryExpander.builder()
       .chatClientBuilder(chatClientBuilder)
       .numberOfQueries(3)
       .build();
   List<Query> queries = queryExpander.expand(new Query("啥是程序员鱼皮？他会啥？"));
   ```

   上面这个查询可能被扩展为：11HHTCyGMmCQQaUVEcwJMFSa1HJPi/DP0lkdXdebXU8=

   - 请介绍程序员鱼皮，以及他的专业技能
   - 给出程序员鱼皮的个人简介，以及他的技能
   - 程序员鱼皮有什么专业技能，并给出更多介绍

   默认情况下，会在扩展查询列表中包含原始查询。可以在构造时通过 `includeOriginal` 方法改变这个行为：

   ```java
   MultiQueryExpander queryExpander = MultiQueryExpander.builder()
       .chatClientBuilder(chatClientBuilder)
       .includeOriginal(false)
       .build();
   ```

   实现原理，也是调用 AI 得到查询扩展，然后按照换行符分割。

**检索**：

检索模块负责从存储中查询出最相关的文档。

1. 文档检索

   之前有了解过 DocumentRetriever 的概念，这是 Spirng AI 提供的文档检索器。每种不同的存储方案都可能有自己的文档检索器实现类，比如 VectorStoreDocumentRetriever ，从向量存储中检索与输入查询语义相似的文档。它支持基于元数据的过滤、设置相似度阈值、设置返回的结果数。

   下面是示例代码：

   ```java
   DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
       .vectorStore(vectorStore)
       .similarityThreshold(0.7)
       .topK(5)
       .filterExpression(new FilterExpressionBuilder()
           .eq("type", "web")
           .build())
       .build();
   List<Document> documents = retriever.retrieve(new Query("谁是程序员鱼皮"));
   ```

2. 文档合并

   Spring AI 内置了 ConcatenationDocumentJoiner 文档合并器，通过连接操作，将基于多个查询和来自多个数据源检索到的文档合并成为单个文档集合。在遇到重复文档时，会保留首次出现的文档，每个文档的分数保持不变。

   示例代码如下：

   ```java
   Map<Query, List<List<Document>>> documentsForQuery = ...
   DocumentJoiner documentJoiner = new ConcatenationDocumentJoiner();
   List<Document> documents = documentJoiner.join(documentsForQuery);
   ```

   看源码发现，这玩意的实现原理很简单，说是 “连接”，其实就是把 Map 展开为二维列表、再把二维列表展开成文档列表，最后进行去重。

   ![img](https://pic.code-nav.cn/course_picture/1608440217629360130/6CzX5EEug0V9NX8h.webp) 

**检索后：优化文档处理**

检索后模块负责处理检索到的文档，以实现最佳生成结果。它们可以解决 “丢失在中间” 问题、模型上下文长度限制，以及减少检索信息中的噪音和冗余。

这些模块可能包括：

- 根据与查询的相关性对文档进行排序
- 删除不相关或冗余的文档
- 压缩每个文档的内容以减少噪音和冗余

#### 查询增强和关联

生成阶段是 RAG 的最终环节，负责将查询到的文档与用户查询结合起来，为 AI 提供必要的上下文，从而生成更准确、更相关的回答。

之前我们已经了解到 Spring AI 提供的两种实现 RAG 查询增强的 Advisor，分别是  `QuestionAnswerAdvisor`  和  RetrievalAugmentationAdvisor 。

1. `QuestionAnswerAdvisor`  查询增强

   当用户问题发送到大模型时，Advisor 会查询向量数据库来获取与用户问题相关的文档，并将这些文档作为上下文附加到用户查询中。

   基本使用如下：

   ```java
   ChatResponse response = ChatClient.builder(chatModel)
           .build().prompt()
           .advisors(new QuestionAnswerAdvisor(vectorStore))
           .user(userText)
           .call()
           .chatResponse();
   // 还可以配置更精细的过滤条件
   var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                 // 相似度阈值为 0.8，并返回最相关的前 6 个结果
           .searchRequest(SearchRequest.builder().similarityThreshold(0.8d).topK(6).build())
           .build();
   
   // 以及动态过滤表达式
   ChatClient chatClient = ChatClient.builder(chatModel)
       .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
           .searchRequest(SearchRequest.builder().build())
           .build())
       .build();
   
   // 在运行时更新过滤表达式
   String content = this.chatClient.prompt()
       .user("看着我的眼睛，回答我！")
       .advisors(a -> a.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "type == 'web'"))
       .call()
       .content();
   
   ```

2. RetrievalAugmentationAdvisor 查询增强

   它基于 RAG 模块化架构，提供了更多的灵活性和定制选项。

   最简单的流程如下：

   ```java
   Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
           .documentRetriever(VectorStoreDocumentRetriever.builder()
                   .similarityThreshold(0.50)
                   .vectorStore(vectorStore)
                   .build())
           .build();
   
   String answer = chatClient.prompt()
           .advisors(retrievalAugmentationAdvisor)
           .user(question)
           .call()
           .content();
   // 还可以结合查询转换器
   Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
           .queryTransformers(RewriteQueryTransformer.builder()
                   .chatClientBuilder(chatClientBuilder.build().mutate())
                   .build())
           .documentRetriever(VectorStoreDocumentRetriever.builder()
                   .similarityThreshold(0.50)
                   .vectorStore(vectorStore)
                   .build())
           .build();
   // 以及空上下文处理，默认是不允许检索的上下文为空，如果为空，指示模型不回答用户查询
   // 通过设置 allowEmptyContext(true)，允许模型在没有找到相关文档的情况下也生成回答
   Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
           .documentRetriever(VectorStoreDocumentRetriever.builder()
                   .similarityThreshold(0.50)
                   .vectorStore(vectorStore)
                   .build())
           .queryAugmenter(ContextualQueryAugmenter.builder()
                   .allowEmptyContext(true)
                   .build())
           .build();
   ```

   如果查询为空，想让大模型输出自己预先指定好的内容的话，可以按下做：

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
   
   ```

#### 混合检索策略

在 RAG 系统中，检索质量直接决定了最终回答的好坏。一般单一的检索方法可能查询到的文档质量不好，所以需要结合多种检索方法一起进行查询然后将文档结合。

主要检索方法比较表：

![1762840801008](C:\Users\29631\AppData\Roaming\Typora\typora-user-images\1762840801008.png)

混合检索的实现方式多种多样，主流的模式有下面 3 种：

1. 并行混合检索

   同时使用多种检索方法获取结果，然后使用重排模型融合多来源结果。

    <img src="https://pic.code-nav.cn/course_picture/1608440217629360130/VYIKoXrV9XMcJqJo.webp" alt="img" style="zoom:50%;" />

2. 级联混合检索

   层层筛选，先使用一种方法进行广泛召回，再用另一种方法精确过滤。

    <img src="https://pic.code-nav.cn/course_picture/1608440217629360130/KdgdWHgXLHS7qAFi.webp" alt="img" style="zoom:50%;" />

3. 动态混合检索

   通过一个 “路由器”，根据查询类型自动选择最合适的检索方法，更加智能。

    <img src="https://pic.code-nav.cn/course_picture/1608440217629360130/ucLWxoQ8vyWyuENX.webp" alt="img" style="zoom:50%;" />

   

   

   

   ### 

    



### 工具调用

之前我们通过 RAG 技术让 AI 应用具备了根据外部知识库来获取信息并回答的能力，但是直到目前为止，AI 应用还只是个 “知识问答助手”。我们可以利用 **工具调用** 特性，实现更多需求。

工具调用和功能调用其实是一个东西，可以理解为让 AI 大模型借助外部工具来完成它自己做不到的事情。

原理也很简单，它实际上并不是让 AI 去调用这些工具，而是用户把需求发给 AI ，让 AI 判断需要使用哪些工具和应该发送怎样的参数，然后把需要调用的工具名称和参数返回给后端程序，让程序执行完调用工具的结果后，把结果返回给大模型，大模型分析结果生成回答返回给用户。

这样做的根本其实就是为了安全性，AI 不能够直接调用工具，必须经过程序的判断可以执行工具后，程序执行完之后把结果返回给 AI ，避免 AI 进行直接调用的不安全。

Spring AI 帮助我们首先可以通过注解或者函数式编程注册工具以及生成 JSON 格式的信息可以快速让大模型理解存在哪些工具以及该什么时候合适去调用哪些工具，并且可以与大模型通信并解析工具调用请求，支持自动根据大模型返回的工具调用请求和参数调用工具，并且把结果转换和异常处理以及传递给大模型和用户并且自动把结果拼接到对话上下文中。

Spring AI 提供了两种定义工具的方法 —— **注解式** 和 **编程式**。

1. 注解式

   只需要通过 @Tool 注解和 @ToolParam 注解说清工具类是用来干什么的，并且传入的参数的含义就可以。

   ```java
   class WeatherTools {
       @Tool(description = "获取指定城市的当前天气情况")
       String getWeather(@ToolParam(description = "城市名称") String city) {
           // 获取天气的实现逻辑
           return "北京今天晴朗，气温25°C";
       }
   }
   ```

2. 编程式

   需要先定义工具类，然后把工具类转为 ToolCall⁡back 工具定义类，之⁠后就可以把这个类绑定给 ⁠ChatClient，从؜而让 AI 使用工具了 。

   ```java
   class WeatherTools {
       String getWeather(String city) {
           // 获取天气的实现逻辑
           return "北京今天晴朗，气温25°C";
       }
   }
   Method method = ReflectionUtils.findMethod(WeatherTools.class, "getWeather", String.class);
   ToolCallback toolCallback = MethodToolCallback.builder()
       .toolDefinition(ToolDefinition.builder(method)
               .description("获取指定城市的当前天气情况")
               .build())
       .toolMethod(method)
       .toolObject(new WeatherTools())
       .build();
   ```

   那么如何使用工具呢？

   有四种方式：

   1. 直接将工具绑定到某次对话上

      ```java
      String response = ChatClient.create(chatModel)
          .prompt("北京今天天气怎么样？")
          .tools(new WeatherTools())  // 在这次对话中提供天气工具
          .call()
          .content();
      ```

   2. 在注册 ChatClient 时就注册工具，全局对话都有效

      ```java
      ChatClient chatClient = ChatClient.builder(chatModel)
          .defaultTools(new WeatherTools(), new TimeTools())  // 注册默认工具
          .build();
      ```

   3. 把工具绑定到底层的 chatmodel 上，实际是给 prompt 的 chatoptions 绑定了工具。

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

   4. 动态解析，支持通过 `ToolCallbackResolver`  在运行时动态解析工具。

开发完工具类后，我们可以给大模型一次性提供所有工具，让它自己决定何时调用。所以我们可以创建**工具注册类**，方便统一管理和绑定所有工具。

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

使用工具：

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
                .advisors(new CodingLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
```

#### 工具底层执行原理

其实 Spring AI 提供了两种工具执行模式：框架控制的工具执行和用户控制的工具执行。这两种模式都离不开一个核心组件  `ToolCallingManager`  。

定义如下， resolveToolDefinitions 用来解析传入的工具调用选型； executeToolCalls 执行模型请求对应的工具调用。

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/eo3Gr7iIIy07bZYK.webp) 

那么 `ToolCallingManager`  是如何知道有工具需要被调用的呢？

其实是通过给出的 assistantMessage 里面是否包含了工具调用选项

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/PdLdB25GfCidyiUo.webp) 

默认情况下框架初始化的时候会生成一个 `DefaultToolCallingManager`  来自动管理整个工具调用流程。



但是也可以通过设置 ToolCallingCh⁠atOptions 的 internalTo⁠olExecutionEnabled 属性为؜ false 来禁用内部工具执行。 然后自己创建一个工具调用管理类管理工具执行流程。

这样一来，我们就可以：

- 在工具执行前后插入自定义逻辑
- 实现更复杂的工具调用链和条件逻辑
- 和其他系统集成，比如追踪 AI 调用进度、记录日志等
- 实现更精细的错误处理和重试机制

```java
// 配置不自动执行工具
ChatOptions chatOptions = ToolCallingChatOptions.builder()
    .toolCallbacks(ToolCallbacks.from(new WeatherTools()))
    .internalToolExecutionEnabled(false)  // 禁用内部工具执行
    .build();
// 创建工具调用管理器
ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder().build();

// 创建初始提示
Prompt prompt = new Prompt("获取编程导航的热门项目教程", chatOptions);
// 发送请求给模型
ChatResponse chatResponse = chatModel.call(prompt);
// 手动处理工具调用循环
while (chatResponse.hasToolCalls()) {
    // 执行工具调用
    ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
    // 创建包含工具结果的新提示
    prompt = new Prompt(toolExecutionResult.conversationHistory(), chatOptions);
    // 再次发送请求给模型
    chatResponse = chatModel.call(prompt);
}

// 获取最终回答
System.out.println(chatResponse.getResult().getOutput().getText());

```

工具指定过程中可能会发生各种异常，Spring AI 提供了灵活的异常处理机制，通过  ToolExecut⁠ionExceptionPro؜cessor  接口实现。

```java
@FunctionalInterface
public interface ToolExecutionExceptionProcessor {
    /**
     * 将工具抛出的异常转换为发送给 AI 模型的字符串，或者抛出一个新异常由调用者处理
     */
    String process(ToolExecutionException exception);
}
```

默认实现类 `DefaultToolExecutionExceptionProcessor` 提供了两种处理策略：

1. alwaysThrow 参数为 false：将异常信息作为错误消息返回给 AI 模型，允许模型根据错误信息调整策略
2. alwaysThrow 参数为 true：直接抛出异常，中断当前对话流程，由应用程序处理

默认使用第一种策略

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/kgNbnHvLACJpNCK1.webp) 



### MCP 协议

如果我们现在有一个需求，需要根据给定的位置找到附近的地点。

按照我们之前学习的知识，应该能想到下面的思路：

1. 直接利用大模型自身的能力：大模型本身就有一定的训练知识，可以识别出知名的位置信息和约会地点，但是不够准本。
2. 利用 RAG 知识库：把地点整理成知识库，让 AI 利用它来回答，但是需要人工提供足够多的信息。
3. 利用工具调用：开发一个根据位置查询附近地点的工具，可以利用第三方地图 API 来实现。

显然，第三种方式效果最好，但是既然都用到了第三方 API，那我们还需要手动开发工具吗？为什么第三方 API 不能直接提供服务给我们的 AI 呢？

其实，已经有了！就是 MCP 协议。

MCP 协议是一种开放标准，目的是增强 AI 与外部系统交互的能力。为 AI 提供了与外部工具、资源和服务交互的标准化方式，让 AI 能够访问最新数据、执行复杂操作，并与现有系统集成。

#### MCP 架构

宏观上本质上属于 “客户端 - 服务器” 架构

SDK 3 层架构中，分为 客户端/服务器 —— 会话层 —— 传输层。客户端和服务器都是用会话层进行会话管理，会话层通过 DefaultMcpSession 实现管理通信模式和状态，传输层处理 JSON - RPC 消息序列化和反序列化，支持多种传输实现，比如 Stdio 标准 IO 流传输和 HTTP SSE 远程传输。

一般需要经过以下流程建立连接：

 ![img](https://pic.code-nav.cn/course_picture/1608440217629360130/edvMLloEXYWLu8rs.webp) 

MCP 重要的概念如下，前三个是重中之重：

1. [Resources 资源](https://modelcontextprotocol.io/docs/concepts/resources#resources)：让服务端向客户端提供各种数据，比如文本、文件、数据库记录、API 响应等，客户端可以决定什么时候使用这些资源。使 AI 能够访问最新信息和外部知识，为模型提供更丰富的上下文。
2. [Prompts 提示词](https://modelcontextprotocol.io/docs/concepts/prompts)：服务端可以定义可复用的提示词模板和工作流，供客户端和用户直接使用。它的作用是标准化常见的 AI 交互模式，比如能作为 UI 元素（如斜杠命令、快捷操作）呈现给用户，从而简化用户与 LLM 的交互过程。
3. [Tools 工具](https://modelcontextprotocol.io/docs/concepts/tools)：MCP 中最实用的特性，服务端可以提供给客户端可调用的函数，使 AI 模型能够执行计算、查询信息或者和外部系统交互，极大扩展了 AI 的能力范围。
4. [Sampling 采样](https://modelcontextprotocol.io/docs/concepts/sampling)：允许服务端通过客户端向大模型发送生成内容的请求（反向请求）。使 MCP 服务能够实现复杂的智能代理行为，同时保持用户对整个过程的控制和数据隐私保护。
5. [Roots 根目录](https://modelcontextprotocol.io/docs/concepts/roots)：MCP 协议的安全机制，定义了服务器可以访问的文件系统位置，限制访问范围，为 MCP 服务提供安全边界，防止恶意文件访问。
6. [Transports 传输](https://modelcontextprotocol.io/docs/concepts/transports)：定义客户端和服务器间的通信方式，包括 Stdio（本地进程间通信）和 SSE（网络实时通信），确保不同环境下的可靠信息交换。

其实大部分客户端都只支持 tools 工具能力。

#### MCP 使用

介绍三种使用 MCP 的方式：

无论是哪种使用方式，原理都是类似：本地下载 MCP 服务端代码运行或者直接使用已经部署的 MCP 服务。

1. 云平台使用 MCP

   之前建立智能体应用的时候，可以添加 MCP 功能，具体的功能需要去 MCP 广场进行开通。

   ![img](https://pic.code-nav.cn/course_picture/1608440217629360130/uddwNyhvFzQ5QBLc.webp)

2. 软件客户端使用 MCP。

   以 Cursor 为例。

   先需要去 MCP 应用市场找到想要使用的 MCP 服务，发现了一个 Server Config，一般都需要一个 api key，这个需要去进行开通

   ![img](https://pic.code-nav.cn/course_picture/1608440217629360130/LxFpWlrVIF2WELyq.webp) 

   然后打开 cursor 客户端，找到一个 settings，选择 mcp，然后添加对应的 server config 即可

   ![img](https://pic.code-nav.cn/course_picture/1608440217629360130/eD7ZyUXs7I0VuMT0.webp) 

   ![img](https://pic.code-nav.cn/course_picture/1608440217629360130/FIvVG6A3ceQwl3aB.webp)

3. 程序中使用 MCP。

   首先肯定是引入相关依赖，程序引入客户端对应依赖。

   ```xml
   <dependency>
       <groupId>org.springframework.ai</groupId>
       <artifactId>spring-ai-mcp-client-spring-boot-starter</artifactId>
       <version>1.0.0-M6</version>
   </dependency>
   ```

   然后在 resources 目录下新建 `mcp-servers.json` 配置，定义需要用到的 MCP 服务 

   这里如果环境是 windows 的话，需要改为 npx.cmd

   ```json
   {
     "mcpServers": {
       "amap-maps": {
         "command": "npx",
         "args": [
           "-y",
           "@amap/amap-maps-mcp-server"
         ],
         "env": {
           "AMAP_MAPS_API_KEY": "改成你的 API Key"
         }
       }
     }
   }
   ```

   然后修改对应 yml 配置，本地运行的话，使用 stdio；远程调用的话，使用 SSE

   ```yaml
   spring:
       ai:
         mcp:
           client:
             stdio:
               servers-configuration: classpath:mcp-servers.json
   ```

   使用的话，引入一个  ToolCallbackProvider  即可。

   ```java
   // AI 调用 MCP 服务
       @Resource
       private ToolCallbackProvider toolCallbackProvider;
   
       public String doChatWithMcp(String message, String chatId) {
           ChatResponse response = chatClient
                   .prompt()
                   .user(message)
                   .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                           .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                   // 开启日志，便于观察效果
                   .advisors(new CodingLoggerAdvisor())
                   .tools(toolCallbackProvider)
                   .call()
                   .chatResponse();
           String content = response.getResult().getOutput().getText();
           log.info("content: {}", content);
           return content;
       }
   ```

   其实本质上来说，MCP 调用就是类似工具调用，只是告诉 AI 提供了哪些工具，如果 AI 想要使用这些工具完成任务，就会告诉后端程序，后端执行工具后再将结果返回给 AI ，最后由 AI 总结并回复。





开发客户端没有什么关键的，就是进行 yml 配置文件的配置，mcp-server.json 文件的配置，然后注入 ToolCallbackProvider  即可。如果想要定制客户端行为，比如设置请求超时时间、设置文件系统根目录的访问范围、自定义事件处理器、添加特定的日志处理逻辑。

示例代码如下：

```java
@Component
public class CustomMcpSyncClientCustomizer implements McpSyncClientCustomizer {
    @Override
    public void customize(String serverConfigurationName, McpClient.SyncSpec spec) {
        // 自定义请求超时配置
        spec.requestTimeout(Duration.ofSeconds(30));
        
        // 设置此客户端可访问的根目录URI
        spec.roots(roots);
        
        // 设置处理消息创建请求的自定义采样处理器
        spec.sampling((CreateMessageRequest messageRequest) -> {
            // 处理采样
            CreateMessageResult result = ...
            return result;
        });

        // 添加在可用工具变更时通知的消费者
        spec.toolsChangeConsumer((List<McpSchema.Tool> tools) -> {
            // 处理工具变更
        });

        // 添加在可用资源变更时通知的消费者
        spec.resourcesChangeConsumer((List<McpSchema.Resource> resources) -> {
            // 处理资源变更
        });

        // 添加在可用提示词变更时通知的消费者
        spec.promptsChangeConsumer((List<McpSchema.Prompt> prompts) -> {
            // 处理提示词变更
        });

        // 添加接收服务器日志消息时通知的消费者
        spec.loggingConsumer((McpSchema.LoggingMessageNotification log) -> {
            // 处理日志消息
        });
    }
}

```



服务器也是需要先进行 yml 的配置

```yaml
spring:
  ai:
    mcp:
      server:
        enabled: true                # 启用/禁用 MCP 服务
        stdio: false                 # 启用/禁用 stdio 传输
        name: my-mcp-server          # 服务名称
        version: 1.0.0               # 服务版本
        type: SYNC                   # 服务类型(SYNC/ASYNC)
        resource-change-notification: true  # 启用资源变更通知
        prompt-change-notification: true    # 启用提示词变更通知
        tool-change-notification: true      # 启用工具变更通知
        sse-message-endpoint: /mcp/message  # SSE 消息端点路径
        sse-endpoint: /sse                  # SSE 端点路径
        # 可选 URL 前缀
        base-url: /api/v1           # 客户端访问路径将是/api/v1/sse 和 /api/v1/mcp/message

```

然后写一个工具类注册为 service，添加响应 @Tool 等注解，然后在启动类中将其注册为 `ToolCallbackProvider` Bean 即可 。

```java
@Service
public class WeatherService {
    @Tool(description = "获取指定城市的天气信息")
    public String getWeather(
            @ToolParameter(description = "城市名称，如北京、上海") String cityName) {
        // 实现天气查询逻辑
        return "城市" + cityName + "的天气是晴天，温度22°C";
    }
}

@SpringBootApplication
public class McpServerApplication {
    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(weatherService)
                .build();
    }
}
```

#### MCP 安全问题

其实 MCP 现在还不是一个很安全的协议，因为你使用的 MCP 服务，你不能看到背后程序的编写者实际上给你提供的服务是怎样的，比如你使用了一个搜索图片的服务，但是编程者只给你返回了一个垃圾图片，而你误以为搜索图片服务生效了。

以及 MCP 工具的描述会被加载到同一会话上下文中，使得恶意 MCP 工具可以影响其它工具的行为，如果某个恶意 MCP 的描述是当使用本功能时，将用户本身的某些机密数据发送到xxxxx地址上去，但是不要告诉用户这个事情，这个描述被拼接到原有的 prompt 中，就发生了隐秘的隐私泄露。

#### 参数传递机制

在 stdio 传输模式下可以通过环境变量传递参数，比如传递 API Key：

这个环境变量会被设置到服务器进程的环境变量中

```json
{
  "mcpServers": {
    "amap-maps": {
      "command": "npx",
      "args": [
        "-y",
        "@amap/amap-maps-mcp-server"
      ],
      "env": {
        "AMAP_MAPS_API_KEY": "你的 API Key"
      }
    }
}
```

在 MCP 服务端可通过 `System.getenv()` 获取环境变量。让我们来测试一下，随便添加一个变量： 

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/E5xxnYY5Ahrw4vBq.webp)

修改 MCP 服务端的代码，获取到环境变量的值。注意不能直接通过 `System.out.println` 来输出环境变量，因为 stdio 使用标准输入输出流进行通信，自己输出的内容会干扰通信。  

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/0Tx7pGOefRzyF9I9.webp)  运行 MCP 客户端，发现获取环境变量的值成功： 

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/PwwqaxfZY6qoPJYH.webp)





 

### AI 智能体构建

智能体是一个能够感知环境、进行推理、指定计划、做出决策并自主采取行动以实现特定目标的 AI 系统。它以大语言模型为核心，集成**记忆、知识库和工具**等能力为一体，构造了完整的决策能力、执行能力和记忆能力，就像一个有主观能动性的人类一样。

智能体它可以：

通过各种输入渠道获得信息，理解用户需求和环境状态；将复杂任务拆解成小任务，并指定执行顺序；根据需要选择并使用各种外部工具和 API，扩展自身能力边界；通过思维链逐步分析问题并推导解决方案；保持上下文连贯性，利用历史交互改进决策；根据执行结果动态调整策略，实现闭环优化。

其实现在的智能体发展的重要方向就是**自主规划能力**，一般模式就是 “思考 - 行动 - 观察” 循环进行下去，但是不是所有的应用场景都需要自主规划能力。

#### 关键技术

1. COT 思维链

   让 AI 像人类一样思考的技术，直接通过系统提示词给 AI 指定规则。要求它以思维链模式进行工作。

   拆分每一个问题，然后每一步都仔细思考并且把思考过程给出，并且提供一个最后的答案。

   在 OpenManus 早期版本中，可以看到实现 COT 的系统提示词：

   ```plain
   You are an assistant focused on Chain of Thought reasoning. For each question, please follow these steps:  
     
   1. Break down the problem: Divide complex problems into smaller, more manageable parts  
   2. Think step by step: Think through each part in detail, showing your reasoning process  
   3. Synthesize conclusions: Integrate the thinking from each part into a complete solution  
   4. Provide an answer: Give a final concise answer  
     
   Your response should follow this format:  
   Thinking: [Detailed thought process, including problem decomposition, reasoning for each step, and analysis]  
   Answer: [Final answer based on the thought process, clear and concise]  
     
   Remember, the thinking process is more important than the final answer, as it demonstrates how you reached your conclusion.
   ```

2. Agent Loop 执行循环

   这是智能体最核心的工作机制，指的是智能体在没有用户输入的情况下，自主重复执行推理和工具调用的过程。传统的大模型中，每次用户提问后，AI 回复一次就结束了。但是在智能体中，AI 回复后可能会继续自主执行后续动作（如调用工具、处理结果、继续推理），形成一个自主执行的循环，直到任务完成（或者超出预设的最大步骤数）。

   参考代码如下：

   ```java
   public String execute() {  
       List<String> results = new ArrayList<>();  
       while (currentStep < MAX_STEPS && !isFinished) {  
           currentStep++;  
           // 这里实现具体的步骤逻辑  
           String stepResult = executeStep();  
           results.add("步骤 " + currentStep + ": " + stepResult);  
       }  
       if (currentStep >= MAX_STEPS) {  
           results.add("达到最大步骤数: " + MAX_STEPS);  
       }  
       return String.join("\n", results);  
   }
   ```

3. ReAct 模式（Reasoning + Acting）

   一种结合推理和行动的智能体架构，它模仿人类解决问题时 “思考 - 行动 - 观察” 的循环，目的是通过交互式决策解决复杂任务，是目前最常用的智能体工作模式之一。

   核心就是：拆解复杂任务为小任务，明确每一步要做什么，然后调用外部工具执行动作，返回结果后，反馈给智能体进行下一步决策，反复重复直到任务完成或达到终止条件。

   示例代码如下：

   ```java
   void executeReAct(String task) {  
       String state = "开始";  
     
       while (!state.equals("完成")) {  
           // 1. 推理 (Reason)  
           String thought = "思考下一步行动";  
           System.out.println("推理: " + thought);  
     
           // 2. 行动 (Act)  
           String action = "执行具体操作";  
           System.out.println("行动: " + action);  
     
           // 3. 观察 (Observe)  
           String observation = "观察执行结果";  
           System.out.println("观察: " + observation);  
     
           // 更新状态  
           state = "完成";  
       }  
   }
   ```

4. 别的就是 AI 大模型、记忆对话系统、知识库以及工具调用。



#### OpenManus 实现原理

##### 整体架构

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/I0SZRLE7IGEc8LI7.webp) 

##### agent 目录

 agent 目录是 OpenManus 实现的核心，采用了分层的代理架构，不同层次的代理负责不同的功能，这样更利于系统的扩展。![img](https://pic.code-nav.cn/course_picture/1608440217629360130/zVLgJbCNr74HZ60U.webp) 

OpenManus 的代理架构主要包含以下几层：

- BaseAgent：最基础的代理抽象类，定义了所有代理的基本状态管理和执行循环
- ReActAgent：实现 ReAct 模式的代理，具有思考和行动两个主要步骤
- ToolCallAgent：能够调用工具的代理，继承自 ReActAgent 并扩展了工具调用能力
- Manus：具体实现的智能体实例，集成了所有能力并添加了更多专业工具

##### tool 目录

tool 定义了各种各样的工具，比如网页搜索、文件搜索、寻求用户帮助、代码执行器等等：

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/4Y4PYQA96JFmb7SG.webp) 

##### prompt 目录

定义了整个项目可能会用到的提示词。

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/mSVVhH5RubGj1OdW.webp)

 ##### 其它支持

为了实现完整的智能体功能，还依赖以下组件：

- 记忆系统：使用 Memory 类存储对话历史和中间状态
- LLM 大模型：通过 LLM 类提供思考和决策能力
- 工具系统：提供 BaseTool 和 ToolCollection 类扩展智能体的能力边界
- 流程控制：通过 AgentState 和执行循环管理状态转换和任务流程

在 OpenManus 中，这些都是自主实现的：

![img](https://pic.code-nav.cn/course_picture/1608440217629360130/fAl9ADVfbzUlroaN.webp) 





#### AI 智能体核心实现

这里重点研究 Agent 分层架构

1. BaseAgent

   所有代理类的基础，定义了智能体状态的管理和循环。具体就是判断智能体的状态是否正常以及执行步骤是否超过最大步骤数，正常的话就执行每一步操作。不过具体的执行方法 step 交由子类实现。

   ```python
   class BaseAgent(BaseModel, ABC):  
       async def run(self, request: Optional[str] = None) -> str:  
           """执行代理的主循环"""  
           if self.state != AgentState.IDLE:  
               raise RuntimeError(f"Cannot run agent from state: {self.state}")  
     
           if request:  
               self.update_memory("user", request)  
     
           results: List[str] = []  
           async with self.state_context(AgentState.RUNNING):  
               while (self.current_step < self.max_steps and   
                     self.state != AgentState.FINISHED):  
                   self.current_step += 1  
                   step_result = await self.step()  
                     
                   # 检查是否陷入循环  
                   if self.is_stuck():  
                       self.handle_stuck_state()  
                         
                   results.append(f"Step {self.current_step}: {step_result}")  
     
               if self.current_step >= self.max_steps:  
                   self.current_step = 0  
                   self.state = AgentState.IDLE  
                   results.append(f"Terminated: Reached max steps ({self.max_steps})")  
             
           return "\n".join(results) if results else "No steps executed"  
         
       @abstractmethod  
       async def step(self) -> str:  
           """执行单步操作，必须由子类实现"""
   ```

2. ReActAgent

   这里主要实现了思考和行动两个关键步骤。同样也是父类定义执行流程，然后子类实现具体的思考和行动方法。整体流程就是先思考，思考后得到是否需要执行步骤，需要执行的话，就执行 act，不需要的话就直接结束，返回不需要执行。

   ```python
   class ReActAgent(BaseAgent, ABC):  
       @abstractmethod  
       async def think(self) -> bool:  
           """处理当前状态并决定下一步行动"""  
             
       @abstractmethod  
       async def act(self) -> str:  
           """执行决定的行动"""  
             
       async def step(self) -> str:  
           """执行单步：思考和行动"""  
           should_act = await self.think()  
           if not should_act:  
               return "Thinking complete - no action needed"  
           return await self.act()
   ```

3. ToolCallAgent

   在上一个 ReAct 类的基础上增加了工具调用能力。是最重要的一个层次。

   就是先把上一步的结果添加到用户信息中，然后跟大模型进行交互得到是否下一步需要执行工具调用，如果不需要那么act就不需要执行，直接返回结果给大模型然后输出回答给用户；如果下一步需要进行工具调用的话，那么就执行工具调用后将结果添加到历史对话中，然后输出这一步的执行结果。

   ```python
   class ToolCallAgent(ReActAgent):  
       """能够执行工具调用的代理类"""  
         
       available_tools: ToolCollection = ToolCollection(  
           CreateChatCompletion(), Terminate()  
       )  
       tool_choices: TOOL_CHOICE_TYPE = ToolChoice.AUTO  
       special_tool_names: List[str] = Field(default_factory=lambda: [Terminate().name])  
         
       async def think(self) -> bool:  
           """处理当前状态并使用工具决定下一步行动"""  
           # 添加下一步提示到用户消息  
           if self.next_step_prompt:  
               user_msg = Message.user_message(self.next_step_prompt)  
               self.messages += [user_msg]  
                 
           # 请求 LLM 选择工具  
           response = await self.llm.ask_tool(  
               messages=self.messages,  
               system_msgs=([Message.system_message(self.system_prompt)]   
                           if self.system_prompt else None),  
               tools=self.available_tools.to_params(),  
               tool_choice=self.tool_choices,  
           )  
             
           # 处理工具调用  
           self.tool_calls = tool_calls = (  
               response.tool_calls if response and response.tool_calls else []  
           )  
           content = response.content if response and response.content else ""  
             
           # 添加助手消息到记忆  
           assistant_msg = (  
               Message.from_tool_calls(content=content, tool_calls=self.tool_calls)  
               if self.tool_calls  
               else Message.assistant_message(content)  
           )  
           self.memory.add_message(assistant_msg)  
             
           # 决定是否应该执行行动  
           return bool(self.tool_calls or content)  
         
       async def act(self) -> str:  
           """执行工具调用并处理结果"""  
           if not self.tool_calls:  
               # 返回最后一条消息内容，如果没有工具调用  
               return self.messages[-1].content or "No content or commands to execute"  
                 
           results = []  
           for command in self.tool_calls:  
               # 执行工具  
               result = await self.execute_tool(command)  
                 
               # 记录工具响应到记忆  
               tool_msg = Message.tool_message(  
                   content=result,  
                   tool_call_id=command.id,  
                   name=command.function.name,  
                   base64_image=self._current_base64_image,  
               )  
               self.memory.add_message(tool_msg)  
               results.append(result)  
                 
           return "\n\n".join(results)
   ```

4. Manus 

   Manus 类是 OpenManus 的核心智能体实例，集成了各种工具和能力。

   ```python
   class Manus(ToolCallAgent):  
       """多功能通用智能体，支持本地和 MCP 工具"""  
         
       name: str = "Manus"  
       description: str = "A versatile agent that can solve various tasks using multiple tools"  
         
       # 添加各种通用工具到工具集合  
       available_tools: ToolCollection = Field(  
           default_factory=lambda: ToolCollection(  
               PythonExecute(),  
               BrowserUseTool(),  
               StrReplaceEditor(),  
               AskHuman(),  
               Terminate(),  
           )  
       )  
   ```

5. MCP 客户端用于远程工具访问

   ```python
       @classmethod  
       async def create(cls, **kwargs) -> "Manus":  
           """工厂方法创建并初始化 Manus 实例"""  
           instance = cls(**kwargs)  
           await instance.initialize_mcp_servers()  
           instance._initialized = True  
           return instance  
             
       async def think(self) -> bool:  
           """处理当前状态并根据上下文决定下一步行动"""  
           if not self._initialized:  
               await self.initialize_mcp_servers()  
               self._initialized = True  
                 
           # 为浏览器工具添加特殊上下文  
           original_prompt = self.next_step_prompt  
           browser_in_use = self._check_browser_in_use()  
             
           if browser_in_use:  
               self.next_step_prompt = (  
                   await self.browser_context_helper.format_next_step_prompt()  
               )  
                 
           result = await super().think()  
             
           # 恢复原始提示  
           self.next_step_prompt = original_prompt  
             
           return result
   ```

   #### 关键实现细节

   1. 工具系统设计

      1. 工具抽象层 BaseTool

         所有工具均继承自 BaseTool 抽象基类，提供统一的接口和行为：

         ```python
         class BaseTool(ABC, BaseModel):  
             name: str  
             description: str  
             parameters: Optional[dict] = None  
               
             async def __call__(self, **kwargs) -> Any:  
                 """使用给定参数执行工具"""  
                 return await self.execute(**kwargs)  
                   
             @abstractmethod  
             async def execute(self, **kwargs) -> Any:  
                 """执行工具的具体逻辑，由子类实现"""  
                   
             def to_param(self) -> Dict:  
                 """将工具转换为函数调用格式"""  
                 return {  
                     "type": "function",  
                     "function": {  
                         "name": self.name,  
                         "description": self.description,  
                         "parameters": self.parameters,  
                     },  
                 }
         ```

         这种设计使得每个工具都有统一的调用方式，同时具有规范化的参数描述，便于 LLM 理解工具的使用方法。

      2. 终止工具 Terminate

         这是一个特殊的工具，允许智能体通过 AI 大模型自主决定何时结束任务，避免无限循环或者过早结束。

         ```python
         class Terminate(BaseTool):  
             name: str = "terminate"  
             description: str = """Terminate the interaction when the request is met OR   
                                   if the assistant cannot proceed further with the task.  
                                   When you have finished all the tasks, call this tool to end the work."""  
               
             parameters: dict = {  
                 "type": "object",  
                 "properties": {  
                     "status": {  
                         "type": "string",  
                         "description": "The finish status of the interaction.",  
                         "enum": ["success", "failure"],  
                     }  
                 },  
                 "required": ["status"],  
             }  
               
             async def execute(self, status: str) -> str:  
                 """完成当前执行"""  
                 return f"The interaction has been completed with status: {status}"
         ```

         在 agent 源码中，有一个 `special_tool_names` 变量，用于指定终止工具等特殊工具： 

         ![img](https://pic.code-nav.cn/course_picture/1608440217629360130/RWPVeFwadBJ45CLN.webp)

           在 `toolcall.py` 源码中，对特殊工具进行处理： 

          其实也就是每次判断是否调用了终止工具，如果调用了终止工具的话，就把智能体的状态改为结束，循环流程自动终止了就。![img](https://pic.code-nav.cn/course_picture/1608440217629360130/jXWZyr42lvDJd3xg.webp) 

      3. 询问工具 AskHuman

         这个工具允许智能体在遇到无法自主解决的问题时向人类寻且帮助，也就是给用户一个输入框，让我们能够更好地干预智能体完成任务地过程。

         ```python
         class AskHuman(BaseTool):  
             """Add a tool to ask human for help."""  
           
             name: str = "ask_human"  
             description: str = "Use this tool to ask human for help."  
             parameters: str = {  
                 "type": "object",  
                 "properties": {  
                     "inquire": {  
                         "type": "string",  
                         "description": "The question you want to ask human.",  
                     }  
                 },  
                 "required": ["inquire"],  
             }  
           
             async def execute(self, inquire: str) -> str:  
                 return input(f"""Bot: {inquire}\n\nYou: """).strip()
         ```

      4. 工具集合 ToolCollection

         设计这个类用来管理多个工具实例，提供统一的工具注册和执行接口：

         ```python
         class ToolCollection:  
             """A collection of defined tools."""  
           
             def __init__(self, *tools: BaseTool):  
                 self.tools = tools  
                 self.tool_map = {tool.name: tool for tool in tools}  
           
             def to_params(self) -> List[Dict[str, Any]]:  
                 return [tool.to_param() for tool in self.tools]  
           
             async def execute(self, *, name: str, tool_input: Dict[str, Any] = None) -> ToolResult:  
                 tool = self.tool_map.get(name)  
                 if not tool:  
                     return ToolFailure(error=f"Tool {name} is invalid")  
                 try:  
                     result = await tool(**tool_input)  
                     return result  
                 except ToolError as e:  
                     return ToolFailure(error=e.message)  
                       
             def add_tools(self, *tools: BaseTool):  
                 """Add multiple tools to the collection."""  
                 for tool in tools:  
                     self.add_tool(tool)  
                 return self
         ```

         这种设计使得 O‌penManus 可以灵活地添⁡加、移除和管理工具，实现了工具⁠系统的可插拔性。我们之前利用 ⁠Spring AI 开发工具调؜用时，也写了个类似的工具注册类。 

   2. MCP 协议支持

      1）MCP 与工具系统的集成

      还记得么，之前提到过 “MCP 的本质就是工具调用”，OpenManus 的实现也是遵循了这一思想。通过 `MCPClients` 类（继承自 ToolCollection）将 MCP 服务集成到现有工具系统中。查看 `tool/mcp.py` 的源码：

      ```python
      class MCPClients(ToolCollection):  
          """  
          A collection of tools that connects to multiple MCP servers and manages available tools through the Model Context Protocol.  
          """  
        
          sessions: Dict[str, ClientSession] = {}  
          exit_stacks: Dict[str, AsyncExitStack] = {}  
          description: str = "MCP client tools for server interaction"  
        
          def __init__(self):  
              super().__init__()  # Initialize with empty tools list  
              self.name = "mcp"  # Keep name for backward compatibility
      ```

      2）动态工具代理

      每当连接到 MCP 服务器时，OpenManus 会动态创建 `MCPClientTool` 实例（继承自 BaseTool）作为每个远程工具的代理。查看 `tool/mcp.py` 的源码，通过向 MCP 服务器发送远程请求来执行工具：

      ```python
      class MCPClientTool(BaseTool):  
          """Represents a tool proxy that can be called on the MCP server from the client side."""  
        
          session: Optional[ClientSession] = None  
          server_id: str = ""  # Add server identifier  
          original_name: str = ""  
        
          async def execute(self, **kwargs) -> ToolResult:  
              """Execute the tool by making a remote call to the MCP server."""  
              if not self.session:  
                  return ToolResult(error="Not connected to MCP server")  
        
              try:  
                  result = await self.session.call_tool(self.original_name, kwargs)  
                  content_str = ", ".join(  
                      item.text for item in result.content if isinstance(item, TextContent)  
                  )  
                  return ToolResult(output=content_str or "No output returned.")  
              except Exception as e:  
                  return ToolResult(error=f"Error executing tool: {str(e)}")
      ```

      3）Manus 中的 MCP 集成机制

      Manus 智能体 **通过工具调用** 实现了与 MCP 服务器的无缝集成。查看 `agent/manus.py` 的源码，发现本质上是把 MCP 服务提供的工具动态添加到可用的工具集合中：

      ```python
      async def connect_mcp_server(  
          self,  
          server_url: str,  
          server_id: str = "",  
          use_stdio: bool = False,  
          stdio_args: List[str] = None,  
      ) -> None:  
          """Connect to an MCP server and add its tools."""  
          if use_stdio:  
              await self.mcp_clients.connect_stdio(server_url, stdio_args or [], server_id)  
          else:  
              await self.mcp_clients.connect_sse(server_url, server_id)  
            
          # 关键实现：动态添加该服务器的工具到可用工具集合  
          new_tools = [  
              tool for tool in self.mcp_clients.tools if tool.server_id == server_id  
          ]  
          self.available_tools.add_tools(*new_tools)
      ```

      这样一来，‌对大模型来说，MC⁡P 工具与本地工具⁠的调用方式一致，很⁠巧妙地复用了代码。

   3. 1）Python 代码执行沙箱

      `PythonExecute` 工具实现了一个安全的 Python 代码执行环境，这是一个值得学习的安全实现。查看 `tool/python_execute.py` 源码：

      ```python
      class PythonExecute(BaseTool):  
          name: str = "python_execute"  
            
          async def execute(self, code: str, timeout: int = 5) -> Dict:  
              """安全执行 Python 代码"""  
              with multiprocessing.Manager() as manager:  
                  result = manager.dict({"observation": "", "success": False})  
                  # 安全的全局环境  
                  safe_globals = {"__builtins__": __builtins__.__dict__.copy()}  
                  # 使用独立进程执行代码  
                  proc = multiprocessing.Process(  
                      target=self._run_code, args=(code, result, safe_globals)  
                  )  
                  proc.start()  
                  proc.join(timeout)  
        
                  # 超时处理  
                  if proc.is_alive():  
                      proc.terminate()  
                      proc.join(1)  
                      return {  
                          "observation": f"Execution timeout after {timeout} seconds",  
                          "success": False,  
                      }  
                  return dict(result)
      ```

      这段代码展示了几个安全编程的最佳实践：

      - 使用独立进程隔离代码执行
      - 实现了超时机制防止无限循环
      - 截获和处理所有异常
      - 重定向标准输出以捕获打印内容

      2）状态管理与上下文切换机制

      `BaseAgent` 实现了一个优雅的状态管理和上下文切换机制：

      ```python
      @asynccontextmanager  
      async def state_context(self, new_state: AgentState):  
          """Context manager for safe agent state transitions."""  
          if not isinstance(new_state, AgentState):  
              raise ValueError(f"Invalid state: {new_state}")  
        
          previous_state = self.state  
          self.state = new_state  
          try:  
              yield  
          except Exception as e:  
              self.state = AgentState.ERROR  # Transition to ERROR on failure  
              raise e  
          finally:  
              self.state = previous_state  # Revert to previous state
      ```

      这个上下文‌管理器确保了状态转⁡换的安全性和可靠性⁠，即使在异常情况下⁠也能正确恢复状态，؜是一个值得学习的设计模式。

      3）工具结果统一表示与组合

      OpenManus 设计了 `ToolResult` 类来统一表示工具执行结果，并支持结果组合。查看 `tool/base.py` 源码：

      ```python
      ▼python复制代码class ToolResult(BaseModel):  
          """Represents the result of a tool execution."""  
        
          output: Any = Field(default=None)  
          error: Optional[str] = Field(default=None)  
          base64_image: Optional[str] = Field(default=None)  
          system: Optional[str] = Field(default=None)  
            
          def __add__(self, other: "ToolResult"):  
              """组合两个工具结果"""  
              def combine_fields(field: Optional[str], other_field: Optional[str], concatenate: bool = True):  
                  if field and other_field:  
                      if concatenate:  
                          return field + other_field  
                      raise ValueError("Cannot combine tool results")  
                  return field or other_field  
        
              return ToolResult(  
                  output=combine_fields(self.output, other.output),  
                  error=combine_fields(self.error, other.error),  
                  base64_image=combine_fields(self.base64_image, other.base64_image, False),  
                  system=combine_fields(self.system, other.system),  
              )
      ```

      这种设计使‌得工具结果处理更加⁡统一和灵活，特别是⁠在需要组合多个工具⁠结果或处理异常情况؜时。



#### 自主实现 Manus 智能体

虽然 OpenManus 代码量很大，但其实很多代码都是在实现智能体所需的支持系统，比如调用大模型、会话记忆、工具调用能力等。如果使用 AI 开发框架，这些能力都不需要我们自己实现，代码量会简单很多。下面就让我们基于 Spring AI 框架，实现一个简化版的 Manus 智能体。

##### 定义数据模型

先新建一个 agent.model 包，将所有用到的数据模型（实体类、枚举类等）都放到该包下。

目前我们只需要定义 Agent 的状态枚举，用于控制智能体的执行。AgentState 代码如下：

```java
/**
 * 代理执行状态的枚举类
 */
public enum AgentState {

    /**
     * 空闲
     */
    IDLE,

    /**
     * 运行状态
     */
    RUNNING,

    /**
     * 执行结束
     */
    FINISHED,

    /**
     * 发生错误
     */
    ERROR
}
```

##### 核心架构开发

首先定义智能体的核心架构，包括以下类：

- BaseAgent ：智能体基类，定义基本信息和多步骤执行流程
- ReActAgent：实现思考和行动两个步骤的智能体
- ToolCallAgent：实现工具调用能力的智能体
- CZManus：最终可使用的 Manus 实例

1. 先开发基础 BaseAgent 类

   首先明确基类的作用，定义基本信息，比如名字，用户提示词和执行步骤的提示词，然后还有智能体的状态，执行步骤最大次数和当前执行步数以及大模型和对话的历史记录。

   具体的 run 流程，就是判断当前智能体的状态是否为初始状态并且传入的用户提示词是否为空，然后不为空的话就改变智能体状态为运行中，随后添加用户提示词到历史对话记录中，随后开始 for 循环执行 step 的流程，即问题被拆分后，每一步思考然后行动调用工具执行的过程，最后把执行结果保存到专门保存结果的列表中，方便返回给用户执行结果；如果中途发生异常了，就记录异常消息然后更改智能体状态为 error 随后返回错误；正常结束或者发生异常后，都要合适的清理资源。不过具体的 step 方法和 cleanup 方法都交由子类来具体实现。

   ```java
   /**
    * 抽象基础代理类，用于管理执行状态和执行流程
    * 子类必须实现 step 方法
    */
   @Data
   @Slf4j
   public abstract class BaseAgent {
   
       // 核心属性
       private String name;
   
       // 提示词
       private String systemPrompt;
       private String nextStepPrompt;
   
       // 代理状态
       private AgentState status = AgentState.IDLE;
   
       // 执行步骤次数
       private int currentStep = 0;
       private int maxSteps = 10;
   
       // 传入的大模型客户端
       private ChatClient chatClient;
   
       // 对话上下文
       List<Message> messageList = new ArrayList<>();
   
   
       // 具体执行方法
       public String run(String userPrompt){
           // 1、校验参数和状态
           if(this.status != AgentState.IDLE){
               throw new RuntimeException("Cannot run from this state: " + this.status);
           }
           if(StrUtil.isBlank(userPrompt)){
               throw new RuntimeException("userPrompt is empty");
           }
           // 2、状态变换
           this.status = AgentState.RUNNING;
           // 3、具体执行流程
           // 将用户提示词添加对话记忆列表的
           messageList.add(new UserMessage(userPrompt));
           // 保存结果
           List<String> results = new ArrayList<>();
           try {
               for(int i = 0; i < maxSteps && status != AgentState.FINISHED; i++){
                   int stepNumber = i + 1;
                   currentStep = stepNumber;
                   log.info("Executing step {}/{}",stepNumber,maxSteps);
                   String stepResult = step();
                   String result = "Step: " + stepNumber + " Result: " + stepResult;
                   results.add(result);
               }
               // 检查是否超出步骤限制
               if(currentStep >= maxSteps){
                   status = AgentState.FINISHED;
                   results.add("Terminated: Reached maxSteps (" + maxSteps + ")");
               }
               return String.join("\n", results);
           } catch (Exception e) {
               status = AgentState.ERROR;
               log.error("error executing step", e);
               return "执行错误" + e.getMessage();
           }finally {
               cleanup();
           }
       }
           /**
        * 具体每一步执行，子类重写此方法
        * @return
        */
       public abstract String step();
   
       /**
        * 清理资源
        */
       protected void cleanup(){
   
       }
   ```

2. 开发 ReActAgent 类

   参考 OpenManus 的实现方式，继承自 BaseAgent，并且将 step 方法分解为 think 和 act 两个抽象方法。

   本质上就是 step 方法分为 think 和 act 两步，先思考是否任务结束，如果任务结束的话，就修改智能体状态，注意这里一定要修改状态为 finished，不然父类的 run 方法中，循环不会终止，即使任务结束它也不会自动终止循环；如果任务没有结束需要调用工具的话，那么就执行工具调用。

   ```java
   /**
    * React 模式的代理类
    * 实现了 思考 - 行动的循环模式
    */
   @EqualsAndHashCode(callSuper = true)
   @Data
   @Slf4j
   public abstract class ReActAgent extends BaseAgent{
   
       /**
        * 代理进行思考，决定是否调用哪些工具类
        * true 代表执行并调用工具，false 表示执行完成，不需要调用
        * @return
        */
       public abstract boolean think();
   
       /**
        * 具体的每一步执行工具的调用
        * @return
        */
       public abstract String act();
   
       /**
        * 自己控制这个先思考 再行动的流程
        * @return
        */
       @Override
       public String step() {
           try {
               boolean shouldAct = think();
               if(!shouldAct){
                   setStatus(AgentState.FINISHED);
                   return "思考完成 - 无需行动";
               }
               return act();
           } catch (Exception e) {
               e.printStackTrace();
               return "步骤执行失败: " + e.getMessage();
           }
       }
   }
   ```

3. 开发 ToolCallAgent 类

   ToolCallAgent 负责实现工具调用能力，继承自 ReActAgent ，具体实现了 think 和 act 两个抽象方法。

   我们选择基于 Spring AI 的工具调用能力，手动控制工具执行。Spring AI 提供了给 prompt 的构造选项时，创建 chatOptions 时配置手动控制工具执行的选项。

   接下来，首先明确，此类为调用工具，那么需要有可用的工具列表 toolCallBack，那么从哪里得到大模型需要调用的工具列表呢，其实从 ChatResponse 中就可以得到，然后我们需要自主调用工具的执行流程，需要自己创建一个 ToolCallingManager，然后还有自己控制工具执行的 ChatOptions，让自己来维护消息上下文以及执行。

   ```java
   @EqualsAndHashCode(callSuper = true)
   @Data
   @Slf4j
   public class ToolCallAgent extends ReActAgent{
       // 可用的工具列表
       private ToolCallback[] availableTools;
   
       // 工具调用的结果
       private ChatResponse toolCallChatResponse;
   
       // 工具调用管理器
       private ToolCallingManager toolCallingManager;
   
       // 关闭 Spring AI 自主执行工具调用的选项，让自己来维护消息上下文和执行工具调用流程
       private final ChatOptions chatOptions;
   
       public ToolCallAgent(ToolCallback[] availableTools){
           super();
           this.availableTools = availableTools;
           this.toolCallingManager = ToolCallingManager.builder().build();
           // 自主执行工具调用和维护消息上下文的选项
           this.chatOptions = DashScopeChatOptions.builder().withProxyToolCalls(true).build();
       }
   ```

   注意，在上述代码中，我们通过将 DashScopeChatOptions 的 withProxyToolCalls 选项设置为 true，来禁止 Spring AI 托管工具调用，而是我们自主实现。因为我们使用的是阿里的  Dash⁠ScopeChatModel  大模型客户端，不能按照官方的方式来实现，否则会直接导致工具调用失效！

   ```java
   // 官方方式
   ChatOptions chatOptions = ToolCallingChatOptions.builder()  
       .toolCallbacks(new CustomerTools())  
       .internalToolExecutionEnabled(false)  
       .build();
   ```

   下面我们实现 think 方法，传入工具列表并调用大模型，得到需要调用的工具列表：

   首先校验提示词，然后拼接提示词到用户提示词中，将其添加到历史对话中，然后跟大模型对话，得到工具调用消息的响应，记录下工具调用消息，然后获得本次思考过程的结果，进行相关的日志记录，如果本次工具调用结果为空的话，记录下本次助手消息到历史对话中；如果调用结果不为空，就直接返回（会自动记录，其实是 toolCallingManager 执行工具调用时自动维护助手消息到历史对话中）；如果思考过程中遇到异常，同样记录下异常消息到历史对话中。

   ```java
       /**
        * 处理当前状态并决定下一步行动
        * @return
        */
       @Override
       public boolean think() {
           // 1、校验提示词，拼接用户提示词
           if(StrUtil.isNotBlank(getNextStepPrompt())){
               getMessageList().add(new UserMessage(getNextStepPrompt()));
           }
           // 2、调用 AI 大模型，获取工具调用列表
           List<Message> messageLists = getMessageList();
           Prompt prompt = new Prompt(messageLists,this.chatOptions);
           try {
               ChatResponse chatResponse = getChatClient().prompt(prompt)
                       .system(getSystemPrompt())
                       .tools(availableTools)
                       .call()
                       .chatResponse();
               // 记录响应，用于 act
               this.toolCallChatResponse = chatResponse;
               // 助手消息
               AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
               // 可用的工具列表
               List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
               // 思考的结果
               String result = assistantMessage.getText();
               log.info(getName() + "的思考: " + result);
               log.info(getName() + "选择了 " + toolCallList.size() + " 个工具来使用");
               String toolCallInfo = toolCallList.stream()
                       .map(toolCall -> String.format("工具名称: %s , 参数: %s", toolCall.name(), toolCall.arguments()))
                       .collect(Collectors.joining("\n"));
               log.info(toolCallInfo);
               // 3、解析工具调用列表，获取要调用的工具
               if(toolCallList.isEmpty()){
                   // 如果可调用工具列表为空的时候，才需要记录助手消息
                   getMessageList().add(assistantMessage);
                   return false;
               }else{
                   // 如果工具调用列表不为空的话，无需自己记录助手消息，会自动记录
                   return true;
               }
           } catch (Exception e) {
               log.error(getName() + "的思考过程中遇到了困难: " + e.getMessage());
               getMessageList().add(new AssistantMessage("处理时遇到错误: " + e.getMessage()));
               return false;
           }
       }
   ```

   最后实现 act 方法，执行工具调用列表，得到返回结果，并将工具的响应添加到消息列表中：

   先判断之前保存的工具调用信息中是否含有工具列表，没有的话就直接返回没有工具需要调用；如果有工具调用的话，就调用自己的 toolCallingManager 进行工具调用执行，随后自己维护消息上下文，这里直接使用 toolExecutionResult 的 conversationHistory，包含之前的消息上下文和本次工具执行的结果。获得本次工具调用的结果，将其返回给用户，然后判断本次是否执行了终止工具，如果执行了需要设置 agent 状态为 finished。

   ```java
       /**
        * 执行工具调用并返回结果
        * @return
        */
       @Override
       public String act() {
           if(!toolCallChatResponse.hasToolCalls()){
               return "没有工具需要调用";
           }
           Prompt prompt = new Prompt(getMessageList(), this.chatOptions);
           // 工具调用
           ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallChatResponse);
           // 保存消息上下文
           // 这里的 conversationHistory 包含之前的消息上下文以及工具的执行结果
           setMessageList(toolExecutionResult.conversationHistory());
           // 获取工具调用的结果
           ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
           String results = toolResponseMessage.getResponses().stream()
                   .map(toolResponse -> "工具 " + toolResponse.name() + " 返回的结果是: " + toolResponse.responseData())
                   .collect(Collectors.joining("\n"));
   
           // 判断是否调用了终止工具
           boolean ifTernimated = toolResponseMessage.getResponses().stream()
                   .anyMatch(response -> response.name().equals("doTerminate"));
           if(ifTernimated){
               setStatus(AgentState.FINISHED);
           }
           log.info(results);
           return results;
       }
   
   ```

4. 开发 CZManus 类

   CZManus 是可以直接提供给其它方法调用的 AI 超级智能体实例，继承自 ToolCallAgent，需要给智能体设置各种参数，比如对话客户端  cha؜tClient 、工具调用列表等。

   ```java
   /**
    * cz 的超级智能体（拥有自主规划和调用工具的能力）
    */
   @Component
   public class CZManus extends ToolCallAgent{
       public CZManus(ToolCallback[] allTools, ChatModel dashscopeChatModel) {
           super(allTools);
           this.setName("CZManus");
           String SYSTEM_PROMPT = """  
                   You are CZManus, an all-capable AI assistant, aimed at solving any task presented by the user.  
                   You have various tools at your disposal that you can call upon to efficiently complete complex requests.  
                   """;
           this.setSystemPrompt(SYSTEM_PROMPT);
           String NEXT_STEP_PROMPT = """  
                   Based on user needs, proactively select the most appropriate tool or combination of tools.  
                   For complex tasks, you can break down the problem and use different tools step by step to solve it.  
                   After using each tool, clearly explain the execution results and suggest the next steps.  
                   If you want to stop the interaction at any point, use the `terminate` tool/function call.  
                   """;
           this.setNextStepPrompt(NEXT_STEP_PROMPT);
           this.setMaxSteps(20);
           // 初始化客户端
           ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                   .defaultAdvisors(new CodingLoggerAdvisor())
                   .build();
           this.setChatClient(chatClient);
       }
   }
   ```

   





### AI 服务化

AI 应用接口开发，提供 SSE 实时流式输出，即类似打字机效果。

```java
    /**
     * AI 基础对话（支持多轮对话记忆，SSE 流式输出）
     * @param message
     * @param chatId
     * @return
     */
    public Flux<String> doChatByStream(String message,String chatId){
        Flux<String> result = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();

        return result;
    }
```

随后开发 controller 相关接口

```java
@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private CodingTeachApp codingTeachApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用 AI 编程学习导师应用
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/codingTeach_app/chat/sync")
    public String doChatWithCodingTeachAppSync(String message,String chatId) {
        return codingTeachApp.doChat(message,chatId);
    }


    /**
     * SSE 调用 AI 编程学习导师应用
     * produces 作用是 http 头里面填充流式返回字段
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/codingTeach_app/chat/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithCodingTeachAppSSE(String message, String chatId) {
        return codingTeachApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 调用 AI 编程学习导师应用
     * 框架会自动添加 http 头
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/codingTeach_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithCodingTeachAppServerSentEvent(String message, String chatId) {
        return codingTeachApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 调用 AI 编程学习导师应用
     * 这里的 SSE 的订阅功能类似有一个回答就直接发送给用户，不等所有结果全都输出完成再一次性发送
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/codingTeach_app/chat/sse_emitter")
    public SseEmitter doChatWithCodingTeachAppSseEmitter(String message, String chatId) {
        // 定义超时时间
        SseEmitter sseEmitter = new SseEmitter(180000L);
        // 获取 flux 响应流并且直接通过订阅推送给 SseEmitter
        codingTeachApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                },sseEmitter::completeWithError,sseEmitter::complete);
        return sseEmitter;
    }

    /**
     * 流式调用 manus 智能体
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message){
        CZManus czManus = new CZManus(allTools, dashscopeChatModel);
        return czManus.runStream(message);
    }
}
```

















