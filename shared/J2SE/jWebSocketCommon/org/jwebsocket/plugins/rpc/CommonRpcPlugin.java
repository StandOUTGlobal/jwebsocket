package org.jwebsocket.plugins.rpc;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.jwebsocket.plugins.rpc.util.ListConverter;
import org.jwebsocket.plugins.rpc.util.MethodMatcherConversionException;
import org.jwebsocket.token.Token;

public class CommonRpcPlugin {
	
	//RpcPlugin namespace
  public static final String NS_RPC_DEFAULT = "org.jwebsocket.plugins.rpc";
  public static final String RRPC_KEY_TARGET_ID = "targetId";
  public static final String RRPC_KEY_SOURCE_ID = "sourceId";
  public static final String RRPC_KEY_CLASSNAME = "classname";
  public static final String RRPC_KEY_SPAWNTHREAD = "spawnThread";
  public static final String RRPC_KEY_METHOD = "method";
  public static final String RRPC_KEY_ARGS = "args";
  
  public static final String RPC_TYPE = "rpc";
  public static final String RRPC_TYPE = "rrpc";

  public static final String RPC_RIGHT_ID = "rpc";
  public static final String RRPC_RIGHT_ID = "rrpc";
}