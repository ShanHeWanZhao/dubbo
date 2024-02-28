/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.demo.GreetingService;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class XmlConsumerApplication {
    /**
     * In order to make sure multicast registry works, need to specify '-Djava.net.preferIPv4Stack=true' before
     * launch the application
     */
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-consumer.xml");
        context.start();
        DemoService demoService = context.getBean("demoService", DemoService.class);
        GreetingService greetingService = context.getBean("greetingService", GreetingService.class);
        System.out.println("Consumer端启动完成，等待输入指令");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.nextLine();
            switch (Integer.parseInt(s)) {
                case 1:
                    try {
                        System.out.println("GreetingService hello Method :" + greetingService.hello());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    CompletableFuture<String> future = demoService.sayHelloAsync("DemoService sayHelloAsync Method");
                    future.whenComplete((s1, throwable) -> {
                        if (throwable != null){
                            throwable.printStackTrace();
                        }
                        System.out.println("当前线程：" + Thread.currentThread().getName());
                        System.out.println(s1);
                    });
                    break;
                case 3:
                    System.out.println(demoService.sayHello("DemoService sayHello Method"));
                    break;
                default:
                    break;
            }
        }
    }
}
