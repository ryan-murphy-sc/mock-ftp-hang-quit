package ftp;

import org.mockftpserver.core.command.Command;
import org.mockftpserver.core.command.CommandNames;
import org.mockftpserver.core.command.InvocationRecord;
import org.mockftpserver.core.session.Session;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.command.AbstractFakeCommandHandler;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HangOnQuitMockFtpServer {

	private static final int CONTROL_PORT = 2121;

	private static final Logger LOGGER = LoggerFactory.getLogger(HangOnQuitMockFtpServer.class);

	public static void main(String[] args) {
		FakeFtpServer fakeFtpServer = new FakeFtpServer();

		//define the command which should never be answered
		fakeFtpServer.setCommandHandler(CommandNames.QUIT, new EverlastingCommandHandler());

		//server config
		fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/ftp/data"));
		FileSystem fileSystem = new UnixFakeFileSystem();
		fileSystem.add(new DirectoryEntry("/ftp/data"));
		fakeFtpServer.setFileSystem(fileSystem);

		//start server
		fakeFtpServer.setServerControlPort(CONTROL_PORT);
		fakeFtpServer.start();
	}

	public static class EverlastingCommandHandler extends AbstractFakeCommandHandler {
		@Override
		protected void handle(Command cmd, Session session) {
			LOGGER.info("Start sleeping for {}", cmd);
			while(true) {
				try {
					LOGGER.info("Sleeping for {}", cmd);
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					//TODO
				}
			}
		}
	};

}