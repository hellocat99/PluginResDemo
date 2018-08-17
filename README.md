# PluginResDemo

这个项目是一个小的Demo:  如何在我们的apk中，使用一个没有安装的apk里面的资源。 

换句话说。Android插件化加载资源的Demo

前提，我把一个没有安装的apk,extra.apk 放到的手机的sd目录。

这里有一点要注意，手机6.0及以上的版本，需要动态的申请读取SD卡的权限.

涉及的知识点：

1. PackageManager的方法getPackageArchiveInfo获取到没有安装的extra.apk的PackageInfo， 

2.通过DexClassLoader 加载没有安装的extra.apk里面的类

DexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent)
//dexPath               有dex的apk,jar等文件的路径（可以没有安装）
//optimizedDirectory  , 给一个路径，DexClassLoader把dex文件加载出来，优化生成Odex文件的目录
//librarySearchPath    ,给null就行
//parent             , DexClassLoader,类加载器的父类

更详细的，见android类加载器的文章

3.反射，获取到没有安装的extra.apk里面的drawable的b.png的id.

4.******重点
生成可以找到extra.apk里面资源的resources

```
private Resources getPluginResources() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, pluginApkPath + File.separator + pluginApkName);
            Resources resources = getResources();
            Resources pluginResources = new Resources(assetManager, resources.getDisplayMetrics()
                    , resources.getConfiguration());
            return pluginResources;

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

```

我们有了能够找到extra.apk里面资源的resources和反射获取到了extra.apk里面图片b.png的id 就可以获取到Drawable对象了

```
Drawable drawable = findPluginDrawable();
                    imageView.setImageDrawable(drawable);

```
