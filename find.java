public class find {
    public static final String PROGRAM_NAME = "find";

    public static void main(String[] args) throws Exception {
        Kernel.initialize();

        if (args.length != 1) {
            Kernel.perror("Usage: find 'path'");
            Kernel.exit(1);
        }

        System.out.println("input path = " + args[0]);
        go(args[0]);
    }

    private static void go(String path) throws Exception {
        // System.out.printf("in go(%s)", path);

        Stat stat = new Stat();
        int status = Kernel.stat(path, stat);
        if (status < 0) {
            Kernel.perror("error in getting file stat");
            Kernel.exit(1);
        }

        short type = (short) (stat.getMode() & Kernel.S_IFMT);

        if (type == Kernel.S_IFREG) {
            System.out.println(path);
        } else if (type == Kernel.S_IFDIR) {
            int fd = Kernel.open(path, Kernel.O_RDONLY);
            if (fd < 0) {
                Kernel.perror("error while opening the directory");
                Kernel.exit(1);
            }

            // System.out.println();
            // System.out.println(path + ":");

            DirectoryEntry dirEntry = new DirectoryEntry();

            while (true) {
                status = Kernel.readdir(fd, dirEntry);
                if (status <= 0)
                    break;

                String entryName = dirEntry.getName();
                String delimiter = path.equals("/") ? "" : "/";
                String fullPath = path + delimiter + entryName;

                if (!entryName.equals(".") && !entryName.equals("..")) {
                    System.out.println(fullPath);
                    go(fullPath);
                }
            }
            Kernel.close(fd);
        }
    }
}
