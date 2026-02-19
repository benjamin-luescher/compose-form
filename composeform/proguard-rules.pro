# ComposeForm Library ProGuard Rules

# Keep the FormField annotation
-keep @interface ch.benlu.composeform.FormField

# Keep all classes that extend Form and their annotated fields
-keep class * extends ch.benlu.composeform.Form {
    <fields>;
}

# Keep all fields annotated with @FormField in any class
-keepclassmembers class * {
    @ch.benlu.composeform.FormField <fields>;
}

# Keep FieldState class and all its members (used via reflection)
-keep class ch.benlu.composeform.FieldState {
    <fields>;
    <methods>;
}

# Keep all Validator implementations
-keep class * extends ch.benlu.composeform.Validator {
    <init>(...);
}

# Keep all Field implementations
-keep class * extends ch.benlu.composeform.Field {
    <init>(...);
}

# Keep PickerValue implementations
-keep class * extends ch.benlu.composeform.fields.PickerValue {
    <fields>;
    <methods>;
}

# Keep annotations attributes for runtime access
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault

# Keep generic type information
-keepattributes Signature

# Keep line numbers for debugging
-keepattributes SourceFile,LineNumberTable