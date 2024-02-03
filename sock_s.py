# 1. 导包
import socket
import time

# 2. 建立连接
server = socket.socket(family=socket.AF_INET, type=socket.SOCK_STREAM)
# 3. 绑定ip和端口 address
server.bind(("172.16.55.159", 20000))
# 3.1 设置端口可重用，不然服务器关闭后几分钟之后才会关闭绑定的端口
"""
    参数说明：
        level：操作socket的级别，若要在API级别操作，选择SOL_SOCKET
        option:操作项，这里是SO_REUSEADDR 标志告诉内核将处于 TIME_WAIT 状态的本地套接字重新使用，而不必等到固有的超时到期
        value:用于访问setsockopt（）的选项值,文档里面是默认给1或者True
"""
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
# 4. 设置监听,128为最大连接数
server.listen(128)
while True:
    try:
        # 5. 等待客户端连接,收到连接后会返回一个专门服务与本次连接的socket和一个地址元组address
        client_socket,address = server.accept()
        # 6. 接收数据
        recv_data = client_socket.recv(1024)
    except:
        client_socket.close()
        continue
    recv_data_str = recv_data.decode('utf-8', errors='ignore')
    if (recv_data_str[:24] != "RGUGttyfgYTghtyuGHYhyTYy"):
        client_socket.close()
        continue
    else:
        recv_data_str = recv_data_str[24:]
    res = time.strftime('%Y%m%d%H%M%S', time.localtime()) + str(address) + ":" + recv_data_str
    print(res)
    with open('andlog', 'a') as f:
        f.write(res + "\n")
    # 7. 响应数据
    #client_socket.send("tfhTFtfTFtgTFGTFtt".encode("utf-8"))
    # 8. 关闭本次连接
    client_socket.close()

# 9.如果需要关闭服务器的话
# server.close()
