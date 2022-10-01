/*
 *
 *
 * e");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lxb.rpc.registry.zk;


import com.lxb.extension.Extension;
import com.lxb.extension.URL;
import com.lxb.extension.condition.ConditionalOnClass;
import com.lxb.rpc.cluster.discovery.backup.Backup;
import com.lxb.rpc.cluster.discovery.naming.AbstractRegistryFactory;
import com.lxb.rpc.cluster.discovery.registry.Registry;

/**
 * @date: 2019/7/19
 */
@Extension(value = "zookeeper")
@ConditionalOnClass({"org.apache.curator.x.async.AsyncCuratorFramework", "org.apache.zookeeper.ZooKeeper"})
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    @Override
    protected Registry createRegistry(String name, URL url, Backup backup) {
        return new ZKRegistry(name, url, backup);
    }
}
