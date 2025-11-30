package design.chain;

public class ChainPatternDemo {
    public static void main(String[] args) {
        AbstractLogger loggerChain = getChainOfLoggers();
        loggerChain.logMessage(AbstractLogger.INFO, "这是一条信息。");
        loggerChain.logMessage(AbstractLogger.ERROR, "这是一条错误信息。");
    }

    // 1. 抽象处理者
    public static abstract class AbstractLogger {
        public static int DEBUG = 1;
        public static int INFO = 2;
        public static int ERROR = 3;

        protected int level;
        protected AbstractLogger nextLogger; // 关键：持有下一个处理者的引用

        public void setNextLogger(AbstractLogger nextLogger) {
            this.nextLogger = nextLogger;
        }

        // 处理请求的核心方法
        public void logMessage(int level, String message) {
            if (this.level == level) {
                write(message);
            }
            if (nextLogger != null) { // 传递给下一个处理者
                nextLogger.logMessage(level, message);
            }
        }

        abstract protected void write(String message);
    }

    // 2. 具体处理者 - 控制台日志
    public static class ConsoleLogger extends AbstractLogger {
        public ConsoleLogger(int level) {
            this.level = level;
        }

        @Override
        protected void write(String message) {
            System.out.println("Standard Console::Logger: " + message);
        }
    }

    // 3. 具体处理者 - 文件日志
    public static class FileLogger extends AbstractLogger {
        public FileLogger(int level) {
            this.level = level;
        }

        @Override
        protected void write(String message) {
            System.out.println("File::Logger: " + message);
        }
    }

    // 4. 具体处理者 - 错误日志
    public static class ErrorLogger extends AbstractLogger {
        public ErrorLogger(int level) {
            this.level = level;
        }

        @Override
        protected void write(String message) {
            System.out.println("Error Console::Logger: " + message);
        }
    }

    // 客户端组装责任链
    private static AbstractLogger getChainOfLoggers() {
        AbstractLogger errorLogger = new ChainPatternDemo.ErrorLogger(AbstractLogger.ERROR);
        AbstractLogger fileLogger = new ChainPatternDemo.FileLogger(AbstractLogger.DEBUG);
        AbstractLogger consoleLogger = new ChainPatternDemo.ConsoleLogger(AbstractLogger.INFO);
        // 构建责任链：ERROR(3) -> DEBUG(2) -> INFO(1)
        // 高级别处理器可以处理低级别及以上的日志
        errorLogger.setNextLogger(fileLogger);
        fileLogger.setNextLogger(consoleLogger);
        return errorLogger;
    }
}
