package com.example.hostelleaveapp.Chat.ChatRepo

import android.annotation.SuppressLint
import android.util.Log
import com.example.hostelleaveapp.DataModel.Chat_all_fields
import com.example.hostelleaveapp.DataModel.HomeChatWarden
import com.example.hostelleaveapp.DataModel.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ChatRepositary {
    private val firebaseFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "ChatRepo"
    suspend fun send_message(content: String, receiverId: String): Boolean {
        val senderId = auth.currentUser!!.uid
        val chatId = if (senderId < receiverId) "${senderId}_${receiverId}" else "${receiverId}_${senderId}"
        return try {
            val message = message(senderId, receiverId, content, System.currentTimeMillis())
            val chatDocRef = firebaseFirestore.collection("chats").document(chatId)

            chatDocRef.set(mapOf(
                "createdBy" to senderId,
                "timestamp" to message.timestamp
            )).await()
            chatDocRef.collection("message").add(message).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message: ${e.message}")
            false
        }
    }

    fun fetchMessage(
        receiverId: String,
        onMessagesChanged: (List<message>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val senderId = auth.currentUser!!.uid
        val chatId = if (senderId < receiverId) "${senderId}_${receiverId}" else "${receiverId}_${senderId}"
        firebaseFirestore.collection("chats")
            .document(chatId)
            .collection("message")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    onError(exception)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(message::class.java) }
                    onMessagesChanged(messages)
                }
            }
    }
    suspend fun fetchWarden(): MutableList<HomeChatWarden> {
        val list = ArrayList<HomeChatWarden>()
        return try {
            Log.d(TAG, "Fetching all wardens...")
            val snapshot = firebaseFirestore.collection("warden").get().await()
            for (document in snapshot.documents) {
                val uid = document.id
                val get_data = document.toObject(HomeChatWarden::class.java)
                if (get_data != null) {
                    val data = HomeChatWarden(
                        name = get_data.name,
                        image = get_data.image,
                        userId = uid
                    )
                    list.add(data)
                }
            }
            Log.d(TAG, "Fetched ${list.size} wardens.")
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching wardens: ${e.message}")
            mutableListOf()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    suspend fun find_nameById(receiverId: String): String {
        return try {
            Log.d(TAG, "🔍 Finding name for ID: $receiverId")
            val snapshotStudent = firebaseFirestore.collection("students")
                .document(receiverId)
                .get()
                .await()

            if (snapshotStudent.exists()) {
                val name = snapshotStudent.getString("name").orEmpty()
                Log.d(TAG, "✅ Found student name: $name")
                return name
            }

            val snapshotWarden = firebaseFirestore.collection("warden")
                .document(receiverId)
                .get()
                .await()

            if (snapshotWarden.exists()) {
                val name = snapshotWarden.getString("name").orEmpty()
                Log.d(TAG, "✅ Found warden name: $name")
                return name
            }

            Log.d(TAG, "❌ No name found for ID: $receiverId")
            ""
        } catch (e: Exception) {
            Log.e(TAG, "❗Error in find_nameById: ${e.message}")
            ""
        }
    }
    suspend fun fetch_message_dataHomeScreen(): MutableList<Chat_all_fields> {
        val list = mutableListOf<Chat_all_fields>()
        try {
            Log.d(TAG, "📥 Fetching chat previews for home screen...")

            val chatSnapshot = firebaseFirestore.collection("chats").get().await()

            Log.d(TAG, "💬 Found ${chatSnapshot.size()} chat documents.")
            val currentUserId = auth.currentUser?.uid ?: ""
            for (doc in chatSnapshot.documents) {
                val chatId = doc.id
                val parts = chatId.split("_")
                if (parts.size < 2) {
                    Log.w(TAG, "❗Invalid chat ID format: $chatId")
                    continue
                }
                val senderId = parts[0]
                val receiverId = parts[1]

                if (currentUserId != senderId && currentUserId != receiverId) {
                    Log.d(TAG, "⛔ Skipping unrelated chat: $chatId")
                    continue
                }
                val otherUserId = if (currentUserId == senderId) receiverId else senderId
                Log.d(TAG, "👤 Other user ID: $otherUserId")
                val messageSnapshot = firebaseFirestore.collection("chats")
                    .document(chatId)
                    .collection("message")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                if (messageSnapshot.isEmpty) {
                    Log.d(TAG, "⚠️ No messages found in chat: $chatId")
                    continue
                }
                val lastMessageObj = messageSnapshot.documents[0].toObject(message::class.java)
                val lastMessage = lastMessageObj?.content ?: "No content"

                val name = find_nameById(otherUserId)
                val image =""
                val timestamp = lastMessageObj?.timestamp ?: 0L

                val chatPreview = Chat_all_fields(
                    name = name,
                    image = image,
                    content = lastMessage,
                    receiverId = otherUserId,
                    timestamp = timestamp
                )
                list.add(chatPreview)
            }
            Log.d(TAG, "✅ Fetched ${list.size} chat previews.")
        } catch (e: Exception) {
            Log.e(TAG, "❗Error fetching home screen messages: ${e.message}")
        }
        return list.sortedByDescending { it.timestamp }.toMutableList()
    }

    suspend fun check_warden_or_student():Boolean{
        var userId = FirebaseAuth.getInstance().currentUser!!.uid
        var boolean:Boolean = false
        return try {
            val studentSnapshot = firebaseFirestore.collection("students").document(userId).get().await()
            if(studentSnapshot.exists())
                boolean = false
            val wardentSnapshot = firebaseFirestore.collection("warden").document(userId).get().await()

            if(wardentSnapshot.exists())
                boolean = true

            Log.d("bool", boolean.toString())
            boolean

        }catch (e:Exception)
        {
            e.printStackTrace()
            false
        }
    }
}
