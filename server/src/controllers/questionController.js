const Question = require('../models/Question');

// Get all questions
exports.getQuestions = async (req, res) => {
  try {
    const questions = await Question.find().sort({ createdAt: -1 });
    res.status(200).json(questions);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// Create a new question
exports.createQuestion = async (req, res) => {
  try {
    const { authorName, content } = req.body;
    const newQuestion = new Question({ authorName, content });
    await newQuestion.save();
    res.status(201).json(newQuestion);
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
};

// Like/Dislike/Comment counts can be added here
